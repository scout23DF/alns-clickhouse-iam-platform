package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record AssignableGrantsRequestDTO(
    String referenceObjectType,
    String resourceId,
    String subjectType,
    String subjectId,

    List<String> rolesNamesRelationsList

) {}
