package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.ResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.*;
import com.clickhouse.alnscodingexercise.wiring.config.OpenFgaProperties;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

public interface IAuthorizationService {

    void addFGAPermission(PermissionSummaryDTO permissionSummary);
    void removeFGAPermission(PermissionSummaryDTO permissionSummary);
    boolean checkFGAPermission(PermissionSummaryDTO permissionSummary);
    boolean checkFGAPermission(String objectId, String objectType, String relation, String userType, String userId);
    List<String> searchGrantedResources(SearchGrantedResourcesParamsDTO searchGrantedResourcesParamsDTO);
    List<String> searchGrantedSubjects(SearchGrantedSubjectsParamsDTO searchGrantedSubjectsParamsDTO);
    List<String> searchGrantedRelations(SearchGrantedRelationsParamsDTO searchGrantedRelationsParamsDTO);

    void loadInitialFgaStructure(OpenFgaProperties openFgaProperties, ResourceLoader resourceLoader) throws Exception;

    List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>> buildEnrichedObjectsWithACLFrom(
            List<ResourceThingDTO> newResourcesThingsList,
            List<String> usernamesList
    );

    List<ProtectedObjectAclDTO> buildAccessControlListFor(ResourceThingDTO resourceThingDTO, List<String> usernamesList);

}
