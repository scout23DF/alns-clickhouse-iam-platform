package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.impl.adapters;

import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.*;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaObjectTypeEnum;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaRelationEnum;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.impl.helpers.LoadInitialFgaDataHelper;
import com.clickhouse.alnscodingexercise.wiring.config.OpenFgaProperties;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.*;
import dev.openfga.sdk.api.configuration.ClientBatchCheckOptions;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import dev.openfga.sdk.errors.FgaValidationError;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class OpenFGAAdapter {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final OpenFgaClient openFgaClient;
    private final LoadInitialFgaDataHelper loadInitialFgaDataHelper;
    private final OpenFgaProperties openFgaProperties;


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
        return createRelationshipsInBatch(List.of(tuple));
    }

    public ResultOperationOpenFgaDTO createRelationshipsInBatch(List<FgaTupleDTO> fgaTuplesToAddList) {

        ResultOperationOpenFgaDTO resultOperationOpenFga = null;
        ClientWriteResponse clientWriteResponse = null;

        try {
            List<ClientTupleKey> clientTupleKeysList = fgaTuplesToAddList.stream()
                    .map(FgaTupleDTO::toClientTupleKey)
                    .toList();

            clientWriteResponse = openFgaClient.writeTuples(clientTupleKeysList).get();
            resultOperationOpenFga = buildResultOperationFromClientResponse(
                    "ADDED_IN_BATCH",
                    fgaTuplesToAddList.stream()
                            .map(PermissionSummaryDTO::of)
                            .toList(),
                    clientWriteResponse
            );

        } catch (FgaInvalidParameterException | InterruptedException | ExecutionException e) {

            resultOperationOpenFga = buildOperationResultFromException("ADDED_IN_BATCH", fgaTuplesToAddList, e);

        }

        LOGGER.info(resultOperationOpenFga.getMessageToLog());

        return resultOperationOpenFga;
    }

    public ResultOperationOpenFgaDTO deleteRelationship(FgaTupleDTO tupleToDelete) {
        return deleteRelationshipInBatch(List.of(tupleToDelete));
    }

    public ResultOperationOpenFgaDTO deleteRelationshipInBatch(List<FgaTupleDTO> fgaTuplesToDeleteList) {

        ResultOperationOpenFgaDTO resultOperationOpenFga = null;
        ClientWriteResponse clientWriteResponse = null;

        try {
            List<ClientTupleKeyWithoutCondition> clientTupleKeysList = fgaTuplesToDeleteList.stream()
                    .map(FgaTupleDTO::toClientTupleKey)
                    .map(tupleKey -> new ClientTupleKeyWithoutCondition()
                            .user(tupleKey.getUser())
                            .relation(tupleKey.getRelation())
                            ._object(tupleKey.getObject())
                    )
                    .toList();

            clientWriteResponse = openFgaClient.deleteTuples(clientTupleKeysList).get();
            resultOperationOpenFga = buildResultOperationFromClientResponse(
                    "DELETED_IN_BATCH",
                    fgaTuplesToDeleteList.stream()
                            .map(PermissionSummaryDTO::of)
                            .toList(),
                    clientWriteResponse
            );

        } catch (FgaInvalidParameterException | InterruptedException | ExecutionException e) {

            resultOperationOpenFga = buildOperationResultFromException("DELETED_IN_BATCH", fgaTuplesToDeleteList, e);

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
        ClientReadRequest clientReadRequest = null;
        ClientReadResponse clientReadResponse = null;
        PermissionSummaryDTO permissionSummaryDTO = null;

        try {

            switch (searchParamsDTO) {
                case SearchGrantedResourcesParamsDTO searchGrantedResourcesParams -> {
                    clientReadRequest = new ClientReadRequest()
                            .user(
                                searchGrantedResourcesParams.getSubjectType()
                                + ":" +
                                searchGrantedResourcesParams.getSubjectId()
                            );

                    permissionSummaryDTO = PermissionSummaryDTO.builder()
                            .subjectType(searchGrantedResourcesParams.getSubjectType())
                            .subjectId(searchGrantedResourcesParams.getSubjectId())
                            .relationshipType(searchGrantedResourcesParams.getRelationshipType())
                            .build();
                }

                case SearchGrantedSubjectsParamsDTO searchGrantedSubjectsParams -> {
                    clientReadRequest = new ClientReadRequest()
                            ._object(
                                searchGrantedSubjectsParams.getResourceType()
                                + ":" +
                                searchGrantedSubjectsParams.getResourceId()
                            );

                    permissionSummaryDTO = PermissionSummaryDTO.builder()
                            .resourceType(searchGrantedSubjectsParams.getResourceType())
                            .resourceId(searchGrantedSubjectsParams.getResourceId())
                            .relationshipType(searchGrantedSubjectsParams.getRelationshipType())
                            .build();

                }

                case SearchGrantedRelationsParamsDTO searchGrantedRelationsParams -> {
                    clientReadRequest = new ClientReadRequest()
                        .user(
                            searchGrantedRelationsParams.getSubjectType()
                            + ":" +
                            searchGrantedRelationsParams.getSubjectId()
                        )
                        ._object(
                            searchGrantedRelationsParams.getResourceType()
                            + ":" +
                            searchGrantedRelationsParams.getResourceId()
                    );

                    permissionSummaryDTO = PermissionSummaryDTO.builder()
                            .subjectType(searchGrantedRelationsParams.getSubjectType())
                            .subjectId(searchGrantedRelationsParams.getSubjectId())
                            .resourceType(searchGrantedRelationsParams.getResourceType())
                            .resourceId(searchGrantedRelationsParams.getResourceId())
                            .build();

                }

                default -> {
                    // Keep behavior: do nothing for unsupported response types.
                }

            }

            clientReadResponse = openFgaClient.read(clientReadRequest).get();

            assert permissionSummaryDTO != null;
            resultOperationOpenFga = buildResultOperationFromClientResponse(
                    "LISTED_OBJECTS",
                    List.of(permissionSummaryDTO),
                    clientReadResponse
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

        PermissionSummaryDTO permissionSummaryDTO = PermissionSummaryDTO.builder()
                .subjectType(userType)
                .subjectId(userId)
                .relationshipType(relation)
                .resourceType(objectType)
                .resourceId(objectId)
                .build();

        return buildResultOperationFromClientResponse(operationName, List.of(permissionSummaryDTO), clientResponse);

    }

    private <TObjClientResponse> ResultOperationOpenFgaDTO buildResultOperationFromClientResponse(
            String operationNameForLogger,
            List<PermissionSummaryDTO> permissionsSummaryList,
            TObjClientResponse clientResponse
    ) {
        ResultOperationOpenFgaDTO.ResultOperationOpenFgaDTOBuilder resultBuilder = ResultOperationOpenFgaDTO.builder();
        String templateMessageToLog = """
                ================================================================
                | ==> OpenFGA : Successfully %s the following ReBAC Permission:
                | - Relationship type = '%s'
                | On:
                | - Resource: type = '%s' :: Id = '%s'
                | To:
                | - User: type = '%s' :: Id = '%s'
                | ================================================================
                """;

        StringBuilder stbMessageToLog = new StringBuilder();
        permissionsSummaryList.forEach(onePermissionSummary -> {
            stbMessageToLog.append(
                   String.format(
                   templateMessageToLog,
                   operationNameForLogger,
                   onePermissionSummary.getRelationshipType(),
                   onePermissionSummary.getResourceType(),
                   onePermissionSummary.getResourceId(),
                   onePermissionSummary.getResourceType(),
                   onePermissionSummary.getSubjectId()
                   )
            );
            stbMessageToLog.append("\n");
        });

        switch (clientResponse) {
            case ClientWriteResponse response -> resultBuilder
                    .statusCode(response.getStatusCode())
                    .rawResponse(response.getRawResponse())
                    .messageToLog(stbMessageToLog.toString());

            case ClientCheckResponse response -> resultBuilder
                    .statusCode(response.getStatusCode())
                    .rawResponse(response.getRawResponse())
                    .checkingResult(response.getAllowed())
                    .messageToLog(stbMessageToLog.toString());

            case ClientListObjectsResponse response -> resultBuilder
                    .statusCode(response.getStatusCode())
                    .rawResponse(response.getRawResponse())
                    .foundObjectsList(response.getObjects())
                    .messageToLog(stbMessageToLog.toString());

            case ClientReadResponse response -> resultBuilder
                    .statusCode(response.getStatusCode())
                    .rawResponse(response.getRawResponse())
                    .foundObjectsList(List.of(response.getRawResponse()))
                    .messageToLog(stbMessageToLog.toString());

            default -> {
                // Keep behavior: do nothing for unsupported response types.
            }
        }

        return resultBuilder.build();
    }

    private ResultOperationOpenFgaDTO buildOperationResultFromException(
            String operationNameForLogger,
            List<FgaTupleDTO> fgaTuplesList,
            Exception occurredException
    ) {

        ResultOperationOpenFgaDTO.ResultOperationOpenFgaDTOBuilder resultBuilder = ResultOperationOpenFgaDTO.builder();
        String templateMessageToLog = """
                ================================================================
                | ==> OpenFGA : The operation '%s' has failed for the following ReBAC Permission:
                | - Relationship type = '%s'
                | On:
                | - Resource: type = '%s' :: Id = '%s' 
                | To:
                | - User: type = '%s' :: Id = '%s' 
                ================================================================
                """;

        StringBuilder stbMessageToLog = new StringBuilder();
        fgaTuplesList.forEach(fgaTuple -> {
            stbMessageToLog.append(
                    String.format(
                            templateMessageToLog,
                            operationNameForLogger,
                            fgaTuple.relation().toString(),
                            fgaTuple.object().type().toString(),
                            fgaTuple.object().id(),
                            fgaTuple.subject().type().toString(),
                            fgaTuple.subject().id()
                    )
            );
            stbMessageToLog.append("\n");
        });

        resultBuilder.occurredException(occurredException)
                .messageToLog(stbMessageToLog.toString());

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
                ================================================================ 
                | ==> OpenFGA : The operation '%s' has failed for the following ReBAC Permission: 
                | - Search Params:
                | %s
                ================================================================
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

    public GenericResultOpenFgaDTO<AllowedActionsDTO> getAllowedActionsOf(PermissionSummaryDTO userResourceTuple) {

        GenericResultOpenFgaDTO<AllowedActionsDTO> resultOperationOpenFga = null;
        ClientBatchCheckRequest clientBatchCheckRequest = null;
        ClientBatchCheckResponse clientBatchCheckResponse = null;

        try {

            clientBatchCheckRequest = new ClientBatchCheckRequest().checks(
                    List.of(
                            new ClientBatchCheckItem()
                                    .user(userResourceTuple.getSubjectType() + ":" + userResourceTuple.getSubjectId())
                                    .relation(FgaRelationEnum.CAN_CHANGE_OWNER.toString())
                                    ._object(userResourceTuple.getResourceType() + ":" + userResourceTuple.getResourceId())
                                    .correlationId("1"),
                            new ClientBatchCheckItem()
                                    .user(userResourceTuple.getSubjectType() + ":" + userResourceTuple.getSubjectId())
                                    .relation(FgaRelationEnum.CAN_WRITE.toString())
                                    ._object(userResourceTuple.getResourceType() + ":" + userResourceTuple.getResourceId())
                                    .correlationId("2"),
                            new ClientBatchCheckItem()
                                    .user(userResourceTuple.getSubjectType() + ":" + userResourceTuple.getSubjectId())
                                    .relation(FgaRelationEnum.CAN_READ.toString())
                                    ._object(userResourceTuple.getResourceType() + ":" + userResourceTuple.getResourceId())
                                    .correlationId("3"),
                            new ClientBatchCheckItem()
                                    .user(userResourceTuple.getSubjectType() + ":" + userResourceTuple.getSubjectId())
                                    .relation(FgaRelationEnum.CAN_SHARE.toString())
                                    ._object(userResourceTuple.getResourceType() + ":" + userResourceTuple.getResourceId())
                                    .correlationId("4"),
                            new ClientBatchCheckItem()
                                    .user(userResourceTuple.getSubjectType() + ":" + userResourceTuple.getSubjectId())
                                    .relation(FgaRelationEnum.CAN_DELETE.toString())
                                    ._object(userResourceTuple.getResourceType() + ":" + userResourceTuple.getResourceId())
                                    .correlationId("5")
                    )
            );

            var options = new ClientBatchCheckOptions()
                    .authorizationModelId(openFgaProperties.getFgaAuthorizationModelId()) // optional, can be set at client level or per request
                    .maxBatchSize(50) // optional, default is 50, can be used to limit the number of checks in a single server request
                    .maxParallelRequests(10); // optional, default is 10, can be used to limit the parallelization of the BatchCheck chunks

            clientBatchCheckResponse = openFgaClient.batchCheck(clientBatchCheckRequest, options).get();

            AllowedActionsDTO allowedActions = AllowedActionsDTO.builder()
                    .canChangeOwner(getAssertionByCorrelationIndex(clientBatchCheckResponse, 1))
                    .canWrite(getAssertionByCorrelationIndex(clientBatchCheckResponse, 2))
                    .canRead(getAssertionByCorrelationIndex(clientBatchCheckResponse, 3))
                    .canShare(getAssertionByCorrelationIndex(clientBatchCheckResponse, 4))
                    .canDelete(getAssertionByCorrelationIndex(clientBatchCheckResponse, 5))
                    .build();

            resultOperationOpenFga = GenericResultOpenFgaDTO.<AllowedActionsDTO>builder()
                    .responseFromFga(allowedActions)
                    .httpStatusCode(HttpStatus.OK.value())
                    .messageToLog(String.format(
                            """
                            ================================================================
                            | ==> OpenFGA : Successfully CHECKED the allowed actions for the user '%s:%s' on the resource '%s:%s' with the following results:
                            | - canChangeOwner = %s
                            | - canWrite = %s
                            | - canRead = %s
                            | - canShare = %s
                            | - canDelete = %s
                            ================================================================
                            """,
                            userResourceTuple.getSubjectType(),
                            userResourceTuple.getSubjectId(),
                            userResourceTuple.getResourceType(),
                            userResourceTuple.getResourceId(),
                            allowedActions.canChangeOwner(),
                            allowedActions.canWrite(),
                            allowedActions.canRead(),
                            allowedActions.canShare(),
                            allowedActions.canDelete()
                    ))
                    .build();

        } catch (FgaInvalidParameterException | InterruptedException | ExecutionException | FgaValidationError e) {

            resultOperationOpenFga = GenericResultOpenFgaDTO.<AllowedActionsDTO>builder()
                    .httpStatusCode(HttpStatus.BAD_REQUEST.value())
                    .occurredException(e)
                    .messageToLog(e.getMessage())
                    .build();

        }

        return resultOperationOpenFga;

    }

    private Boolean getAssertionByCorrelationIndex(ClientBatchCheckResponse clientBatchCheckResponse, int index) {
        return clientBatchCheckResponse.getResult().stream()
                .filter(result -> result.getCorrelationId().equals(String.valueOf(index)))
                .findFirst()
                .map(ClientBatchCheckSingleResponse::isAllowed)
                .orElseGet(() -> {
                    LOGGER.warn("No assertion found in BatchCheckResponse for correlationId: {}. Defaulting to false.", index);
                    return Boolean.FALSE;
                });
    }

}
