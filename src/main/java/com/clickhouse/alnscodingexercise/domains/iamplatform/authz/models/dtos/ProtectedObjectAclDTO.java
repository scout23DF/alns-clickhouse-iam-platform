package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record ProtectedObjectAclDTO(
    String refenceObjectType,
    String resourceId,
    String subjectType,
    String subjectId,

    AllowedActionsDTO allowedActions,

    List<String> rolesNamesRelationsList
    // List<String> grantedPermissionsRelationsList

) {}
