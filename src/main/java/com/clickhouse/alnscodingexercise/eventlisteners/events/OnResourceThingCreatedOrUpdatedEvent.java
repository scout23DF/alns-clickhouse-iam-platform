package com.clickhouse.alnscodingexercise.eventlisteners.events;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.ResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.AssignableGrantsRequestDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class OnResourceThingCreatedOrUpdatedEvent extends ApplicationEvent {

    private final ResourceThingDTO resourceThingDTO;
    private final List<AssignableGrantsRequestDTO> accessControlList;

    public OnResourceThingCreatedOrUpdatedEvent(ResourceThingDTO resourceThingDTO, List<AssignableGrantsRequestDTO> accessControlList) {
        super(resourceThingDTO);
        this.resourceThingDTO = resourceThingDTO;
        this.accessControlList = accessControlList;
    }

}
