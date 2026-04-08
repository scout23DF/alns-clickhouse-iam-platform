package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.impl.adapters;

import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.*;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaObjectTypeEnum;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaRelationEnum;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.impl.helpers.LoadInitialFgaDataHelper;
import com.clickhouse.alnscodingexercise.wiring.config.OpenFgaProperties;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.*;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class OpenFGAAdapter {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final OpenFgaClient openFgaClient;
    private final LoadInitialFgaDataHelper loadInitialFgaDataHelper;


    public ResultOperationOpenFgaDTO createRelationship(String objectId,
                                                        String objectType,
                                                        String relation,
                                                        String userType,
                                                        String userId) {

        FgaTupleDTO fgaTupleDTO = FgaTupleDTO.of(
                FgaObjectDTO.of(FgaObjectTypeEnum.valueOf(objectType), objectId),
                FgaRelationEnum.valueOf(relation),
                FgaObjectDTO.of(FgaObjectTypeEnum.valueOf(userType), userId)
        );

        return createRelationship(fgaTupleDTO);
    }

    public ResultOperationOpenFgaDTO createRelationship(FgaTupleDTO tuple) {

        ResultOperationOpenFgaDTO resultOperationOpenFga = null;
        ClientWriteResponse clientWriteResponse = null;

        try {

            clientWriteResponse = openFgaClient.writeTuples(List.of(tuple.toClientTupleKey())).get();
            resultOperationOpenFga = buildResultOperationFromClientResponse(
                    "ADDED",
                    tuple,
                    clientWriteResponse
            );

        } catch (FgaInvalidParameterException | InterruptedException | ExecutionException e) {

            resultOperationOpenFga = buildOperationResultFromException("ADDED", tuple, e);

        }

        LOGGER.info(resultOperationOpenFga.getMessageToLog());

        return resultOperationOpenFga;
    }

    public ResultOperationOpenFgaDTO deleteRelationship(FgaTupleDTO tuple) {

        ResultOperationOpenFgaDTO resultOperationOpenFga = null;
        ClientWriteResponse clientWriteResponse = null;

        try {

            clientWriteResponse = openFgaClient.deleteTuples(List.of(tuple.toClientTupleKey())).get();
            resultOperationOpenFga = buildResultOperationFromClientResponse(
                    "DELETED",
                    tuple,
                    clientWriteResponse
            );

        } catch (FgaInvalidParameterException | InterruptedException | ExecutionException e) {

            resultOperationOpenFga = buildOperationResultFromException("DELETED", tuple, e);

        }

        LOGGER.info(resultOperationOpenFga.getMessageToLog());

        return resultOperationOpenFga;
    }

    public ResultOperationOpenFgaDTO check(String objectId,
                                         String objectType,
                                         String relation,
                                         String userType,
                                         String userId) {

        ResultOperationOpenFgaDTO resultOperationOpenFga = null;
        ClientCheckRequest clientCheckRequest = null;
        ClientCheckResponse clientCheckResponse = null;

        try {

            clientCheckRequest = new ClientCheckRequest()
                .user(String.format("%s:%s", userType, userId))
                .relation(relation)
                ._object(String.format("%s:%s", objectType, objectId));

            clientCheckResponse = openFgaClient.check(clientCheckRequest).get();
            resultOperationOpenFga = buildResultOperationFromClientResponse(
                    "CHECKED",
                    objectId,
                    objectType,
                    relation,
                    userType,
                    userId,
                    clientCheckResponse
            );

        } catch (FgaInvalidParameterException | InterruptedException | ExecutionException e) {

            resultOperationOpenFga = buildOperationResultFromException(
                    "CHECKED",
                    objectId,
                    objectType,
                    relation,
                    userType,
                    userId,
                    e);
        }

        LOGGER.info(resultOperationOpenFga.getMessageToLog());

        return resultOperationOpenFga;

    }

    public <TObjSearchParamsDTO> ResultOperationOpenFgaDTO getObjects(TObjSearchParamsDTO searchParamsDTO) {

        ResultOperationOpenFgaDTO resultOperationOpenFga = null;
        ClientListObjectsRequest clientListObjectsRequest = null;
        ClientListObjectsResponse clientListObjectsResponse = null;
        FgaTupleDTO fgaTupleDTO = null;

        try {

            switch (searchParamsDTO) {
                case SearchGrantedResourcesParamsDTO searchGrantedResourcesParams -> {
                    clientListObjectsRequest = new ClientListObjectsRequest()
                        .user(searchGrantedResourcesParams.getSubjectId())
                        .relation(searchGrantedResourcesParams.getRelationshipType())
                        .type(searchGrantedResourcesParams.getResourceType());

                    fgaTupleDTO = FgaTupleDTO.of(
                            FgaObjectDTO.of(
                                    FgaObjectTypeEnum.valueOf(searchGrantedResourcesParams.getSubjectType().toLowerCase()),
                                    searchGrantedResourcesParams.getSubjectId()
                            ),
                            FgaRelationEnum.valueOf(searchGrantedResourcesParams.getRelationshipType()),
                            FgaObjectDTO.of(
                                    FgaObjectTypeEnum.valueOf(searchGrantedResourcesParams.getResourceType().toLowerCase()),
                                    "[All]"
                            )
                    );
                }

                case SearchGrantedSubjectsParamsDTO searchGrantedSubjectsParams -> {
                    clientListObjectsRequest = new ClientListObjectsRequest()
                            .context(searchGrantedSubjectsParams.getResourceId())
                            .relation(searchGrantedSubjectsParams.getRelationshipType())
                            .type(searchGrantedSubjectsParams.getSubjectType());

                    fgaTupleDTO = FgaTupleDTO.of(
                            FgaObjectDTO.of(
                                    FgaObjectTypeEnum.valueOf(searchGrantedSubjectsParams.getSubjectType().toLowerCase()),
                                    "[All]"
                            ),
                            FgaRelationEnum.valueOf(searchGrantedSubjectsParams.getRelationshipType()),
                            FgaObjectDTO.of(
                                    FgaObjectTypeEnum.valueOf(searchGrantedSubjectsParams.getResourceType().toLowerCase()),
                                    searchGrantedSubjectsParams.getResourceId()
                            )
                    );

                }

                case SearchGrantedRelationsParamsDTO searchGrantedRelationsParams -> {
                    clientListObjectsRequest = new ClientListObjectsRequest()
                            .user(searchGrantedRelationsParams.getSubjectId())
                            .context(searchGrantedRelationsParams.getResourceId());

                    fgaTupleDTO = FgaTupleDTO.of(
                            FgaObjectDTO.of(
                                    FgaObjectTypeEnum.valueOf(searchGrantedRelationsParams.getSubjectType().toLowerCase()),
                                    searchGrantedRelationsParams.getSubjectId()
                            ),
                            null,
                            FgaObjectDTO.of(
                                    FgaObjectTypeEnum.valueOf(searchGrantedRelationsParams.getResourceType().toLowerCase()),
                                    searchGrantedRelationsParams.getResourceId()
                            )
                    );

                }

                default -> {
                    // Keep behavior: do nothing for unsupported response types.
                }

            }

            clientListObjectsResponse = openFgaClient.listObjects(clientListObjectsRequest).get();
            assert fgaTupleDTO != null;
            resultOperationOpenFga = buildResultOperationFromClientResponse(
                    "LISTED_OBJECTS",
                    fgaTupleDTO,
                    clientListObjectsResponse
            );

        } catch (FgaInvalidParameterException | InterruptedException | ExecutionException e) {

            resultOperationOpenFga = buildOperationResultFromException(
                    "LIST_OBJECTS",
                    searchParamsDTO,
                    e
            );

        }

        LOGGER.info(resultOperationOpenFga.getMessageToLog());

        return resultOperationOpenFga;

    }


    private <TObjClientResponse>  ResultOperationOpenFgaDTO buildResultOperationFromClientResponse(String operationName,
                                                                                   String objectId,
                                                                                   String objectType,
                                                                                   String relation,
                                                                                   String userType,
                                                                                   String userId,
                                                                                   TObjClientResponse clientResponse) {

        FgaTupleDTO fgaTupleDTO = FgaTupleDTO.of(
                FgaObjectDTO.of(FgaObjectTypeEnum.valueOf(objectType.toUpperCase()), objectId),
                FgaRelationEnum.valueOf(relation.toUpperCase()),
                FgaObjectDTO.of(FgaObjectTypeEnum.valueOf(userType.toUpperCase()), userId)
        );

        return buildResultOperationFromClientResponse(operationName, fgaTupleDTO, clientResponse);

    }

    private <TObjClientResponse> ResultOperationOpenFgaDTO buildResultOperationFromClientResponse(
            String operationNameForLogger,
            FgaTupleDTO tuple,
            TObjClientResponse clientResponse
    ) {
        ResultOperationOpenFgaDTO.ResultOperationOpenFgaDTOBuilder resultBuilder = ResultOperationOpenFgaDTO.builder();
        String templateMessageToLog = """
                \n ================================================================ \n 
                | ==> OpenFGA : Successfully %s the following ReBAC Permission: \n
                | \t - Relationship type = '%s' \n
                | On: \n 
                | \t - Resource: type = '%s' :: Id = '%s' \n
                | To: \n 
                | \t - User: type = '%s' :: Id = '%s' \n 
                | ================================================================ \n ";
                """;

        String messageToLogFulfilled = String.format(
                templateMessageToLog,
                operationNameForLogger,
                tuple.relation().toString(),
                tuple.object().type().toString(),
                tuple.object().id(),
                tuple.subject().type().toString(),
                tuple.subject().id()
        );

        switch (clientResponse) {
            case ClientWriteResponse response -> resultBuilder
                    .statusCode(response.getStatusCode())
                    .rawResponse(response.getRawResponse())
                    .messageToLog(messageToLogFulfilled);

            case ClientCheckResponse response -> resultBuilder
                    .statusCode(response.getStatusCode())
                    .rawResponse(response.getRawResponse())
                    .checkingResult(response.getAllowed())
                    .messageToLog(messageToLogFulfilled);

            case ClientListObjectsResponse response -> resultBuilder
                    .statusCode(response.getStatusCode())
                    .rawResponse(response.getRawResponse())
                    .foundObjectsList(response.getObjects())
                    .messageToLog(messageToLogFulfilled);

            default -> {
                // Keep behavior: do nothing for unsupported response types.
            }
        }

        return resultBuilder.build();
    }

    private ResultOperationOpenFgaDTO buildOperationResultFromException(
            String operationNameForLogger,
            FgaTupleDTO fgaTuple,
            Exception occurredException
    ) {

        ResultOperationOpenFgaDTO.ResultOperationOpenFgaDTOBuilder resultBuilder = ResultOperationOpenFgaDTO.builder();
        String templateMessageToLog = """
                \n ================================================================ \n
                | ==> OpenFGA : The operation '%s' has failed for the following ReBAC Permission: \n
                | \t - Relationship type = '%s' \n 
                | On: \n  
                | \t - Resource: type = '%s' :: Id = '%s' \n" 
                | To: \n 
                | \t - User: type = '%s' :: Id = '%s' \n 
                ================================================================ \n ;
                """;

        String messageToLogFulfilled = String.format(
                templateMessageToLog,
                operationNameForLogger,
                fgaTuple.relation().toString(),
                fgaTuple.object().type().toString(),
                fgaTuple.object().id(),
                fgaTuple.subject().type().toString(),
                fgaTuple.subject().id()
        );

        resultBuilder.occurredException(occurredException)
                .messageToLog(messageToLogFulfilled);

        return resultBuilder.build();
    }

    private ResultOperationOpenFgaDTO buildOperationResultFromException(
            String operationName,
            String objectId,
            String objectType,
            String relation,
            String userType,
            String userId,
            Exception occurredException) {

        FgaTupleDTO fgaTupleDTO = FgaTupleDTO.of(
                FgaObjectDTO.of(FgaObjectTypeEnum.valueOf(objectType.toUpperCase()), objectId),
                FgaRelationEnum.valueOf(relation.toUpperCase()),
                FgaObjectDTO.of(FgaObjectTypeEnum.valueOf(userType.toUpperCase()), userId)
        );

        return buildOperationResultFromException(operationName, fgaTupleDTO, occurredException);

    }

    private <TObjSearchParamsDTO> ResultOperationOpenFgaDTO buildOperationResultFromException(
            String operationNameForLogger,
            TObjSearchParamsDTO searchParamsDTO,
            Exception occurredException
    ) {

        ResultOperationOpenFgaDTO.ResultOperationOpenFgaDTOBuilder resultBuilder = ResultOperationOpenFgaDTO.builder();
        String templateMessageToLog = """
                \n ================================================================ \n 
                | ==> OpenFGA : The operation '%s' has failed for the following ReBAC Permission: 
                | \t - Search Params: \n
                | %s \n
                | ================================================================ \n
                """;

        String messageToLogFulfilled = String.format(
                templateMessageToLog,
                operationNameForLogger,
                searchParamsDTO
        );

        resultBuilder.occurredException(occurredException)
                .messageToLog(messageToLogFulfilled);

        return resultBuilder.build();
    }

    public void loadInitialFgaStructure(OpenFgaProperties openFgaProperties,
                                        ResourceLoader resourceLoader) throws Exception {
        this.loadInitialFgaDataHelper.loadInitialFgaStructure(
            openFgaProperties,
            openFgaClient,
            resourceLoader
        );
    }
}
