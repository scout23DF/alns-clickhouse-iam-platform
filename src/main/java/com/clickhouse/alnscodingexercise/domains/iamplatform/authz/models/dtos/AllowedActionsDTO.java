package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos;

import lombok.Builder;

@Builder(toBuilder = true)
public record AllowedActionsDTO(

    String objectType,
    String objectId,

    Boolean canChangeOwner,
    Boolean canDelete,
    Boolean canShare,
    Boolean canWrite,
    Boolean canRead
) {}
