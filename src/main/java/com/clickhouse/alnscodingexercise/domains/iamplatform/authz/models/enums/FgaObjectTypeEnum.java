package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums;

public enum FgaObjectTypeEnum {
    USER("user"),
    GROUP("group"),
    DOCUMENT("document");

    private final String value;

    FgaObjectTypeEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}