package com.clickhouse.alnscodingexercise.eventlisteners.events;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.ResourceThingDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationEvent;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class OnResourceThingRemovedEvent extends ApplicationEvent {

    private final ResourceThingDTO resourceThingDTO;

    public OnResourceThingRemovedEvent(ResourceThingDTO resourceThingDTO) {
        super(resourceThingDTO);
        this.resourceThingDTO = resourceThingDTO;
    }

}
