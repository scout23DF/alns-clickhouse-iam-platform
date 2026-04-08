package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.impl;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.ResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.*;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaObjectTypeEnum;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaRelationEnum;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.IAuthorizationService;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.impl.adapters.OpenFGAAdapter;
import com.clickhouse.alnscodingexercise.wiring.config.OpenFgaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements IAuthorizationService {

    private final OpenFGAAdapter openFGAAdapter;

    @Override
    public void addFGAPermission(PermissionSummaryDTO permissionSummary) {
        ResultOperationOpenFgaDTO resultOperationOpenFgaDTO;
        FgaTupleDTO fgaTuple;

        fgaTuple = FgaTupleDTO.of(
                FgaObjectDTO.of(
                        FgaObjectTypeEnum.valueOf(permissionSummary.getSubjectType().toUpperCase()),
                        permissionSummary.getSubjectId()
                ),
                FgaRelationEnum.valueOf(permissionSummary.getRelationshipType().toUpperCase()),
                FgaObjectDTO.of(
                        FgaObjectTypeEnum.valueOf(permissionSummary.getResourceType().toUpperCase()),
                        permissionSummary.getResourceId()
                        // , FgaRelationEnum.valueOf(permissionSummary.getResourceRelationshipType().toUpperCase())
                )
        );

        resultOperationOpenFgaDTO = openFGAAdapter.createRelationship(fgaTuple);

        if (resultOperationOpenFgaDTO.getOccurredException() != null) {
            throw new AuthorizationServiceException(
                    "Failed to add Permission On Protected Resource",
                    resultOperationOpenFgaDTO.getOccurredException()
            );
        }
    }

    @Override
    public void removeFGAPermission(PermissionSummaryDTO permissionSummary) {
        ResultOperationOpenFgaDTO resultOperationOpenFgaDTO;
        FgaTupleDTO fgaTuple = FgaTupleDTO.of(permissionSummary);

        resultOperationOpenFgaDTO = openFGAAdapter.deleteRelationship(FgaTupleDTO.of(permissionSummary));

        if (resultOperationOpenFgaDTO.getOccurredException() != null) {
            throw new AuthorizationServiceException(
                    "Error performing FGA ReBAC deletion for FgaTuple: " + fgaTuple,
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

    @Override
    public List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>> buildEnrichedObjectsWithACLFrom(
            List<ResourceThingDTO> newResourcesThingsList,
            List<String> usernamesList
    ) {

        return newResourcesThingsList.stream()
                .map(resourceThingDTO -> GenericObjectEnrichedWithACLDTO.<ResourceThingDTO>builder()
                        .itemProtected(resourceThingDTO)
                        .protectedObjectsAclsList( this.buildAccessControlListFor(resourceThingDTO, usernamesList) )
                        .build())
                .toList();
    }

    @Override
    public List<ProtectedObjectAclDTO> buildAccessControlListFor(ResourceThingDTO resourceThingDTO, List<String> usernamesList) {

        return null;
    }

}
