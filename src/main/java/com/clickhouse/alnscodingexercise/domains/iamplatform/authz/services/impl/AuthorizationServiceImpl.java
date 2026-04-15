package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.impl;

import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.*;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.IAuthorizationService;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.impl.adapters.OpenFGAAdapter;
import com.clickhouse.alnscodingexercise.wiring.config.OpenFgaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements IAuthorizationService {

    private final OpenFGAAdapter openFGAAdapter;

    @Override
    public void addFGAPermissionsInBatch(List<PermissionSummaryDTO> permissionSummaryDTOList) {
        ResultOperationOpenFgaDTO resultOperationOpenFgaDTO;

        resultOperationOpenFgaDTO = openFGAAdapter.createRelationshipsInBatch(
                FgaTupleDTO.of(permissionSummaryDTOList)
        );

        if (resultOperationOpenFgaDTO.getOccurredException() != null) {
            throw new AuthorizationServiceException(
                    "Failed to add Permission On Protected Resource",
                    resultOperationOpenFgaDTO.getOccurredException()
            );
        }
    }

    @Override
    public void removeFGAPermissionsInBatch(List<PermissionSummaryDTO> permissionSummaryDTOList) {
        ResultOperationOpenFgaDTO resultOperationOpenFgaDTO;

        resultOperationOpenFgaDTO = openFGAAdapter.deleteRelationshipInBatch(
                FgaTupleDTO.of(permissionSummaryDTOList)
        );

        if (resultOperationOpenFgaDTO.getOccurredException() != null) {
            throw new AuthorizationServiceException(
                    "Error performing FGA ReBAC deletion for FgaTuples: " + FgaTupleDTO.of(permissionSummaryDTOList),
                    resultOperationOpenFgaDTO.getOccurredException()
            );
        }

    }

    @Override
    public List<String> searchGrantedResources(SearchGrantedResourcesParamsDTO searchGrantedResourcesParamsDTO) {
        return doSearchGenericGranted(searchGrantedResourcesParamsDTO);
    }

    @Override
    public List<String> searchGrantedSubjects(SearchGrantedSubjectsParamsDTO searchGrantedSubjectsParamsDTO) {
        return doSearchGenericGranted(searchGrantedSubjectsParamsDTO);
    }

    @Override
    public List<String> searchGrantedRelations(SearchGrantedRelationsParamsDTO searchGrantedRelationsParamsDTO) {
        return doSearchGenericGranted(searchGrantedRelationsParamsDTO);
    }

    @Override
    public boolean checkFGAPermission(PermissionSummaryDTO permissionSummary) {
        return checkFGAPermission(
                permissionSummary.getResourceId(),
                permissionSummary.getResourceType(),
                permissionSummary.getRelationshipType(),
                permissionSummary.getSubjectType(),
                permissionSummary.getSubjectId()
        );
    }

    @Override
    public boolean checkFGAPermission(String objectId, String objectType, String relation, String userType, String userId) {

        ResultOperationOpenFgaDTO resultOperationOpenFgaDTO;

        resultOperationOpenFgaDTO = openFGAAdapter.check(objectId, objectType, relation, userType, userId);

        if (resultOperationOpenFgaDTO.getOccurredException() != null) {
            throw new AuthorizationServiceException(
                    "Error performing FGA check for objectId: " + objectId + ", objectType: " + objectType + ", relation: " + relation + ", userType: " + userType + ", userId: " + userId,
                    resultOperationOpenFgaDTO.getOccurredException()
            );
        }

        return resultOperationOpenFgaDTO.getCheckingResult();
    }

    @Override
    public AllowedActionsDTO getAllowedActionsOnObjectForUser(PermissionSummaryDTO userResourceToVerify) {

        GenericResultOpenFgaDTO<AllowedActionsDTO> resultOperationOpenFgaDTO;

        resultOperationOpenFgaDTO = openFGAAdapter.getAllowedActionsOf(userResourceToVerify);

        if (resultOperationOpenFgaDTO.occurredException() != null) {
            log.warn("Error getting allowed actions for {}. Defaulting all actions to false.", userResourceToVerify,
                    resultOperationOpenFgaDTO.occurredException());

            return AllowedActionsDTO.builder()
                    .objectType(userResourceToVerify.getResourceType())
                    .objectId(userResourceToVerify.getResourceId())
                    .canChangeOwner(Boolean.FALSE)
                    .canDelete(Boolean.FALSE)
                    .canShare(Boolean.FALSE)
                    .canWrite(Boolean.FALSE)
                    .canRead(Boolean.FALSE)
                    .build();
        }

        return resultOperationOpenFgaDTO.responseFromFga();
    }


    private <TObjSearchParamsDTO> List<String> doSearchGenericGranted(TObjSearchParamsDTO searchGrantedGenericParamsDTO) {
        ResultOperationOpenFgaDTO resultOperationOpenFgaDTO;

        resultOperationOpenFgaDTO = openFGAAdapter.getObjects(searchGrantedGenericParamsDTO);

        if (resultOperationOpenFgaDTO.getOccurredException() != null) {
            throw new AuthorizationServiceException(
                    "Failed to get Objects in FGA for the search params: " + searchGrantedGenericParamsDTO,
                    resultOperationOpenFgaDTO.getOccurredException()
            );
        }

        return resultOperationOpenFgaDTO.getFoundObjectsList();
    }

    @Override
    public void loadInitialFgaStructure(OpenFgaProperties openFgaProperties,
                                        ResourceLoader resourceLoader) throws Exception {
        this.openFGAAdapter.loadInitialFgaStructure(openFgaProperties, resourceLoader);
    }

}
