package com.clickhouse.alnscodingexercise.domains.iamplatform.authn.models.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class ActiveUserStoreDTO {
    public List<String> users = new ArrayList<>();
}
