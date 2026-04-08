package com.clickhouse.alnscodingexercise.domains.assetmgmt.services.impl;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.ResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.entities.ResourceThing;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.repositories.ResourceThingRepository;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.services.IResourceThingMgmtService;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.services.mappers.ResourceThingMapper;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.web.requests.CommandResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.repositories.UserRepository;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.GenericObjectEnrichedWithACLDTO;
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

        ResourceThing newResourceThing = resourceThingMapper.commandRequestToEntity(commandResourceThing);

        resourceThingRepository.save(newResourceThing);

        ResourceThingDTO resourceThingDTO = resourceThingMapper.entityToDTO(newResourceThing);

        eventPublisher.publishEvent(
                OnResourceThingCreatedOrUpdatedEvent.builder()
                        .resourceThingDTO(resourceThingDTO)
                        .accessControlList(commandResourceThing.protectedObjectsAclsList())
                        .build()
        );

        return resourceThingDTO;
    }

    @Override
    public List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>> createResourcesThingsList(
            List<CommandResourceThingDTO> commandsResourceThingsDTOList
    ) {
        List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>> enrichedWithACLDTOList = new ArrayList<>();
        List<String> filteredUsernamesList = List.of();

        commandsResourceThingsDTOList.forEach(oneCommandResourceThing -> {
            ResourceThingDTO resourceThingDTO =this.createResourceThing(oneCommandResourceThing);

            enrichedWithACLDTOList.add(
                    GenericObjectEnrichedWithACLDTO.<ResourceThingDTO>builder()
                            .itemProtected(resourceThingDTO)
                            .protectedObjectsAclsList(
                                    authorizationUtils.buildAccessControlListFrom(
                                            resourceThingDTO,
                                            oneCommandResourceThing.protectedObjectsAclsList(),
                                            filteredUsernamesList
                                    )
                            ).build()
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

}
