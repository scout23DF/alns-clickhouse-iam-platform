package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.impl.helpers;

import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.config.OpenFgaClientTupleKeyDeserializer;
import com.clickhouse.alnscodingexercise.wiring.config.OpenFgaProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientReadAuthorizationModelResponse;
import dev.openfga.sdk.api.client.model.ClientTupleKey;
import dev.openfga.sdk.api.client.model.ClientWriteRequest;
import dev.openfga.sdk.api.configuration.ClientReadAuthorizationModelOptions;
import dev.openfga.sdk.api.configuration.ClientWriteOptions;
import dev.openfga.sdk.api.model.CreateStoreRequest;
import dev.openfga.sdk.api.model.Store;
import dev.openfga.sdk.api.model.WriteAuthorizationModelRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class LoadInitialFgaDataHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private volatile ResourceLoader resourceLoader;
    private OpenFgaProperties openFgaProperties;
    private OpenFgaClient openFgaClient;

    private final ObjectMapper jsonObjectMapper = new ObjectMapper()
            .registerModule(
                    new SimpleModule().addDeserializer(
                            ClientTupleKey.class,
                            new OpenFgaClientTupleKeyDeserializer()
                    )
            );


    public void loadInitialFgaStructure(OpenFgaProperties openFgaProperties,
                                        OpenFgaClient openFgaClient,
                                        ResourceLoader resourceLoader) throws Exception {

        this.openFgaClient = openFgaClient;
        this.openFgaProperties = openFgaProperties;
        this.resourceLoader = resourceLoader;

        if (StringUtils.hasText(openFgaProperties.getFgaStoreId())) {

            validateStore(openFgaProperties.getFgaStoreId());

            if (StringUtils.hasText(openFgaProperties.getFgaAuthorizationModelId())) {

                validateAuthorizationModelId(openFgaProperties.getFgaAuthorizationModelId());

            } else {

                var latestAuthModel = getLatestAuthorizationModelId();

                logger.debug("Authorization Check resolved with a Status Code of {} and {}",
                        latestAuthModel.getStatusCode(), latestAuthModel.getAuthorizationModel());

                if (latestAuthModel.getStatusCode() == 200) {
                    openFgaProperties.setFgaAuthorizationModelId(latestAuthModel.getAuthorizationModel().getId());
                }
            }

        } else {

            if (openFgaProperties.isFgaShouldImportInitialStructure()) {

                openFgaProperties.setFgaStoreId(makeStore());

                logger.debug("Failed to find an Authorization Model, checking for a Schema at {}", openFgaProperties.getFgaInitialModelSchemaToImport());

                var script = resourceLoader.getResource(openFgaProperties.getFgaInitialModelSchemaToImport());

                logger.trace("Writing the following Authorization Model Schema: \n{}", script.getContentAsString(StandardCharsets.UTF_8));

                var authWriteResponse = openFgaClient.writeAuthorizationModel(
                        jsonObjectMapper.readValue(
                                script.getContentAsByteArray(),
                                WriteAuthorizationModelRequest.class
                        )
                ).get();

                logger.debug("Authorization Model Creation Request responded with a Status Code of {} and {}",
                        authWriteResponse.getStatusCode(),
                        authWriteResponse.getAuthorizationModelId()
                );

                openFgaProperties.setFgaAuthorizationModelId(authWriteResponse.getAuthorizationModelId());

                for (String relationshipFile : openFgaProperties.getFgaInitialRelationshipTuplesToImport()) {
                    Resource resource = resourceLoader.getResource(relationshipFile);

                    logger.debug("Adding new Relationship Tuple, \n{}",
                            resource.getContentAsString(StandardCharsets.UTF_8)
                    );

                    var clientWriteRequest = jsonObjectMapper.readValue(
                            resource.getContentAsByteArray(),
                            ClientWriteRequest.class
                    );

                    setUpRelationshipTuples(clientWriteRequest);
                } // for

            } // if shouldImportInitialStructure

        }
    }

    private ClientReadAuthorizationModelResponse getLatestAuthorizationModelId() throws Exception { // Way too broad, but the fail cases are extreme in this.
        return openFgaClient.readLatestAuthorizationModel().get();
    }

    private void setUpRelationshipTuples(ClientWriteRequest request) {
        // Write
        try {
            logger.debug("Writing Tuples");
            openFgaClient
                    .write(request,
                            new ClientWriteOptions()
                                    .disableTransactions(true)
                                    .authorizationModelId(openFgaProperties.getFgaAuthorizationModelId()))
                    .get();
            logger.debug("Done Writing Tuples");
        } catch (Exception e) {
            logger.error("Failed to write {} due to Exception {}, application will now fail to start.", request, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void validateStore(String storeId) throws Exception {
        Objects.requireNonNull(storeId);
        var invalidStore = openFgaClient.listStores().get()
                .getStores()
                .stream()
                .map(Store::getId)
                .noneMatch(storeId::equals);
        if (invalidStore) {
            throw new IllegalArgumentException("The Store ID: " + storeId + " does not exist.");
        }
    }

    private void validateAuthorizationModelId(String authId) throws Exception {
        Objects.requireNonNull(authId);
        var storeAuthModelResponse = openFgaClient.readAuthorizationModel(new ClientReadAuthorizationModelOptions().authorizationModelId(authId))
                .get();
        logger.debug("Validating Authorization Model {}, Status Code {}, Response {}", authId, storeAuthModelResponse.getStatusCode(), storeAuthModelResponse.getAuthorizationModel());
        switch (HttpStatus.valueOf(storeAuthModelResponse.getStatusCode())) {
            case HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND ->
                    throw new IllegalStateException("Failed to find the Authorization Model for " + authId);
            case HttpStatus.CONFLICT ->
                    throw new IllegalStateException("Transaction Conflict within OpenFGA for Authorization Model:  " + authId);
            case HttpStatus.INTERNAL_SERVER_ERROR ->
                    throw new IllegalStateException("OpenFGA Server had an internal failure when checking for the Authorization Model of " + authId);
        }
    }

    private String makeStore() {
        String storeName = openFgaProperties.getFgaStoreName();
        Objects.requireNonNull(storeName, "Failed to have a Store ID or Store Name provided, OpenFGA will fail to start.");
        String newStoreId;
        try {
            logger.debug("Created a new Store with the name {}", storeName);
            var newStore = openFgaClient.createStore(new CreateStoreRequest().name(storeName)).get();
            logger.debug("Store Name {}, Status Code {}, Response {}", storeName, newStore.getStatusCode(), newStore.getRawResponse());
            openFgaClient.setStoreId(newStore.getId());
            newStoreId = newStore.getId();
        } catch (Exception e) {
            logger.error("Failed to create a new OpenFGA Store", e);
            throw new RuntimeException(e);
        }
        return newStoreId;
    }

}
