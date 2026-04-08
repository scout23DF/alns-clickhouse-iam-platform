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

    public static PermissionSummaryDTO of(FgaTupleDTO oneFgaTuple) {
        return PermissionSummaryDTO.builder()
            .subjectType(oneFgaTuple.subject().type().toString())
            .subjectId(oneFgaTuple.subject().id())
            .relationshipType(oneFgaTuple.relation().toString())
            .resourceType(oneFgaTuple.object().type().toString())
            .resourceId(oneFgaTuple.object().id())
            .build();
    }
}
