package com.clickhouse.alnscodingexercise.domains.shared.models.enums;

public enum GenericOperationResultEnum {
    SUCCESS("success"),
    ERROR("error"),
    FAILED("failed");

    private final String value;

    GenericOperationResultEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
