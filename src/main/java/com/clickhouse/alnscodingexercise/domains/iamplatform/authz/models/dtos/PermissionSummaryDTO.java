package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class PermissionSummaryDTO {

    private String subjectType;
    private String subjectId;
    private String subjectShortDescription;

    private String relationshipType;

    private String resourceType;
    private String resourceId;
    private String resourceShortDescription;
    private String resourceRelationshipType;

}
