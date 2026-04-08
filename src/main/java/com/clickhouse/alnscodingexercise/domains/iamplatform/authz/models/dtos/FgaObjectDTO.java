package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos;

import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaGroupEnum;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaObjectTypeEnum;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaRelationEnum;

public record FgaObjectDTO(FgaObjectTypeEnum type, String id, FgaRelationEnum relation) {
    public FgaObjectDTO {
        if (id == null) {
            throw new IllegalArgumentException("Id is required");
        }
    }

    public static FgaObjectDTO of(String id) {
        return new FgaObjectDTO(FgaObjectTypeEnum.USER, id, null);
    }

    public static FgaObjectDTO of(FgaObjectTypeEnum type, String id) {
        return new FgaObjectDTO(type, id, null);
    }

    public static FgaObjectDTO of(FgaObjectTypeEnum type, String id, FgaRelationEnum relation) {
        return new FgaObjectDTO(type, id, relation);
    }

    public static FgaObjectDTO of(FgaGroupEnum group) {
        return new FgaObjectDTO(FgaObjectTypeEnum.GROUP, group.toString(), null);
    }

    public static FgaObjectDTO of(FgaGroupEnum group, FgaRelationEnum relation) {
        return new FgaObjectDTO(FgaObjectTypeEnum.GROUP, group.toString(), relation);
    }

    private String toStringWithoutRelation() {
        return type.toString() + ":" + id;
    }

    private String toValue() {
        return relation == null
                ? toStringWithoutRelation()
                : toStringWithoutRelation() + "#" + relation;
    }

    @Override
    public String toString() {
        return toValue();
    }
}
