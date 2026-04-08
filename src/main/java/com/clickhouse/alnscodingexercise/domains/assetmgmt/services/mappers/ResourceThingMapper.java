package com.clickhouse.alnscodingexercise.domains.assetmgmt.services.mappers;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.ResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.entities.ResourceThing;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.web.requests.CommandResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ResourceThingMapper {

    private final UserRepository userRepository;

    public ResourceThing commandRequestToEntity(CommandResourceThingDTO commandResourceThing) {

        ResourceThing mappedResourceThing = null;

        if (commandResourceThing != null) {
            mappedResourceThing = new ResourceThing();
            mappedResourceThing.setId(commandResourceThing.id());
            mappedResourceThing.setTitle(commandResourceThing.title());
            mappedResourceThing.setMetadata(commandResourceThing.metadata());
            mappedResourceThing.setSummaryContent(commandResourceThing.summaryContent());
            mappedResourceThing.setFullContent(commandResourceThing.fullContent());
            mappedResourceThing.setCreatedAt(Instant.now());
            mappedResourceThing.setUpdatedAt(Instant.now());

            if (StringUtils.isNotEmpty(commandResourceThing.creatorUsername())) {
                mappedResourceThing.setUserCreator(getUsernameOf(commandResourceThing.creatorUsername()));
            }
        }

        return mappedResourceThing;
    }

    public ResourceThingDTO entityToDTO(ResourceThing resourceThingEntity) {
        ResourceThingDTO mappedResourceThing = null;

        if (resourceThingEntity != null) {
            mappedResourceThing = ResourceThingDTO.builder()
                    .id(resourceThingEntity.getId())
                    .title(resourceThingEntity.getTitle())
                    .metadata(resourceThingEntity.getMetadata())
                    .summaryContent(resourceThingEntity.getSummaryContent())
                    .fullContent(resourceThingEntity.getFullContent())
                    .createdAt((Objects.nonNull(resourceThingEntity.getCreatedAt()) ? resourceThingEntity.getCreatedAt().toString() : null))
                    .updatedAt((Objects.nonNull(resourceThingEntity.getUpdatedAt()) ? resourceThingEntity.getUpdatedAt().toString() : null))
                    .creatorUsername(resourceThingEntity.getUserCreator() != null ? resourceThingEntity.getUserCreator().getUsername() : null)
                    .build();
        }

        return mappedResourceThing;
    }

    private CHUserAccount getUsernameOf(String pUsername) {
        return userRepository.findByUsername(pUsername);
    }

    public ResourceThing updateEntityWithCommandRequest(ResourceThing foundResourceThing,
                                                        CommandResourceThingDTO commandResourceThing) {

        if (commandResourceThing != null) {
            foundResourceThing.setTitle(commandResourceThing.title());
            foundResourceThing.setMetadata(commandResourceThing.metadata() + " :: [Updated]");
            foundResourceThing.setSummaryContent(commandResourceThing.summaryContent() + " :: [Updated]");
            foundResourceThing.setFullContent(commandResourceThing.fullContent() + " :: [Updated]");
            foundResourceThing.setUpdatedAt(Instant.now());

            if (StringUtils.isNotEmpty(commandResourceThing.creatorUsername())
                && (!commandResourceThing.creatorUsername().equals(foundResourceThing.getUserCreator().getUsername()))
                ) {
                foundResourceThing.setUserCreator(getUsernameOf(commandResourceThing.creatorUsername()));
            }
        }

        return foundResourceThing;
    }
}
