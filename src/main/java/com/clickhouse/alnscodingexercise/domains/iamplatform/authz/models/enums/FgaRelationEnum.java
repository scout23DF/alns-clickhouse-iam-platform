package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums;

public enum FgaRelationEnum {
    MEMBER("member"),
    OWNER("owner"),
    EDITOR("editor"),
    VIEWER("viewer"),
    CAN_CHANGE_OWNER("can_change_owner"),
    CAN_DELETE("can_delete"),
    CAN_SHARE("can_share"),
    CAN_WRITE("can_write"),
    CAN_READ("can_read");

    private final String value;

    FgaRelationEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
