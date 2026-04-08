package com.clickhouse.alnscodingexercise.domains.assetmgmt.services.impl;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.ResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.entities.ResourceThing;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.repositories.ResourceThingRepository;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.services.IResourceThingMgmtService;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.services.mappers.ResourceThingMapper;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.web.requests.CommandResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.repositories.UserRepository;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.GenericObjectEnrichedWithACLDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.ProtectedObjectAclDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.IAuthorizationService;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.validation.AuthorizationUtils;
import com.clickhouse.alnscodingexercise.domains.shared.models.enums.GenericOperationResultEnum;
import com.clickhouse.alnscodingexercise.eventlisteners.events.OnResourceThingCreatedOrUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceThingMgmtServiceImpl implements IResourceThingMgmtService {

    private final ResourceThingRepository resourceThingRepository;
    private final UserRepository userRepository;
    private final ResourceThingMapper resourceThingMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final IAuthorizationService authorizationService;
    private final AuthorizationUtils authorizationUtils;

    @Override
    public ResourceThingDTO createResourceThing(CommandResourceThingDTO commandResourceThing) {
        return doCreateOrUpdateResourceThing(commandResourceThing, true);
    }

    @Override
    public List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>> createResourcesThingsList(
            List<CommandResourceThingDTO> commandsResourceThingsDTOList
    ) {
        List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>> enrichedWithACLDTOList = new ArrayList<>();
        List<String> filteredUsernamesList = List.of();

        commandsResourceThingsDTOList.forEach(oneCommandResourceThing -> {

            ResourceThingDTO resourceThingDTO = this.doCreateOrUpdateResourceThing(oneCommandResourceThing, false);

            List<ProtectedObjectAclDTO> aclListOfResourceList = authorizationUtils.synchronizeAclOf(
                    oneCommandResourceThing.shouldIgnoreAclContent(),
                    resourceThingDTO,
                    oneCommandResourceThing.assignableGrantsRequestsList()
            );

            enrichedWithACLDTOList.add(
                    GenericObjectEnrichedWithACLDTO.<ResourceThingDTO>builder()
                            .itemProtected(resourceThingDTO)
                            .protectedObjectsAclsList(aclListOfResourceList)
                            .build()
            );
        });

        return enrichedWithACLDTOList;

    }

    @Override
    public ResourceThingDTO updateResourceThing(CommandResourceThingDTO commandResourceThing) {
        throw new NotImplementedException();
    }

    @Override
    public List<ResourceThingDTO> searchByContainingText(String searchText, String propertyToSearchUpon) {
        return List.of();
    }

    @Override
    public List<GenericOperationResultEnum> removeResourceThingsByIds(List<String> idsToRemoveList) {
        return List.of();
    }

    @Override
    public List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>> searchByContainingTextWithACL(String searchText,
                                                                                                 String propertyToSearchUpon) {
        return List.of();
    }

    private ResourceThingDTO doCreateOrUpdateResourceThing(CommandResourceThingDTO commandResourceThing,
                                                           boolean shouldTriggerPermissionsEvent) {

        ResourceThing persistableEntity;

        ResourceThingDTO resourceThingDTOResult = resourceThingRepository.findById(commandResourceThing.id())
                .map(foundResourceThing -> {

                    ResourceThing updatedResourceThing = resourceThingMapper.updateEntityWithCommandRequest(
                            foundResourceThing, commandResourceThing
                    );
                    resourceThingRepository.save(updatedResourceThing);
                    return resourceThingMapper.entityToDTO(updatedResourceThing);

                })
                . orElseGet(() -> {

                    ResourceThing foundResourceThing = resourceThingMapper.commandRequestToEntity(commandResourceThing);
                    resourceThingRepository.save(foundResourceThing);
                    return resourceThingMapper.entityToDTO(foundResourceThing);

                });

        if (shouldTriggerPermissionsEvent) {
            eventPublisher.publishEvent(
                    OnResourceThingCreatedOrUpdatedEvent.builder()
                            .resourceThingDTO(resourceThingDTOResult)
                            .accessControlList(commandResourceThing.assignableGrantsRequestsList())
                            .build()
            );
        }

        return resourceThingDTOResult;
    }

}
