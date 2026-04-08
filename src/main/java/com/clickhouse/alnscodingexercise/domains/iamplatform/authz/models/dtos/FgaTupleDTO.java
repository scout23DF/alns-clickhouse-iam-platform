package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos;

import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaObjectTypeEnum;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaRelationEnum;
import dev.openfga.sdk.api.client.model.ClientTupleKey;

public record FgaTupleDTO(FgaObjectDTO subject, FgaRelationEnum relation, FgaObjectDTO object) {
    public FgaTupleDTO {
        if (subject == null || relation == null || object == null) {
            throw new IllegalArgumentException("All fields must be non-null");
        }
    }

    public static FgaTupleDTO of(FgaObjectDTO subject, FgaRelationEnum relation, FgaObjectDTO object) {
        return new FgaTupleDTO(subject, relation, object);
    }

    public static FgaTupleDTO of(String subject, FgaRelationEnum relation, String object) {
        return new FgaTupleDTO(FgaObjectDTO.of(subject), relation, FgaObjectDTO.of(object));
    }

    public ClientTupleKey toClientTupleKey() {
        return new ClientTupleKey()
                .user(subject.toString())
                .relation(relation.toString())
                ._object(object.toString());
    }

    public static FgaTupleDTO of(PermissionSummaryDTO permissionSummary) {
        return FgaTupleDTO.of(
                FgaObjectDTO.of(
                        FgaObjectTypeEnum.valueOf(permissionSummary.getSubjectType().toLowerCase()),
                        permissionSummary.getSubjectId()
                ),
                FgaRelationEnum.valueOf(permissionSummary.getRelationshipType()),
                FgaObjectDTO.of(
                        FgaObjectTypeEnum.valueOf(permissionSummary.getResourceType().toLowerCase()),
                        permissionSummary.getResourceId()
                )
        );
    }
}