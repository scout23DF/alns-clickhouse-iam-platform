package com.clickhouse.alnscodingexercise.domains.assetmgmt.services;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.ResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.web.requests.CommandResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.GenericObjectEnrichedWithACLDTO;
import com.clickhouse.alnscodingexercise.domains.shared.models.enums.GenericOperationResultEnum;

import java.util.List;

public interface IResourceThingMgmtService {

    ResourceThingDTO createResourceThing(CommandResourceThingDTO commandResourceThing);
    List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>> createResourcesThingsList(List<CommandResourceThingDTO> commandsResourceThingsDTOList);
    ResourceThingDTO updateResourceThing(CommandResourceThingDTO commandResourceThing);
    List<ResourceThingDTO> searchByContainingText(String searchText, String propertyToSearchUpon);
    List<GenericOperationResultEnum> removeResourceThingsByIds(List<String> idsToRemove);

    List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>> searchByContainingTextWithACL(
            String searchText,
            String propertyToSearchUpon,
            List<String> usersIdsToFilterList
    );

    List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>> listAllWithAclForUser(CHUserAccount authenticatedUser);
}
