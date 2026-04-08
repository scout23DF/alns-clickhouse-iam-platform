package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums;

public enum FgaGroupEnum {
    ADMIN("admin"),
    EDITOR("editor"),
    VIEWER("viewer");

    private final String value;

    FgaGroupEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
