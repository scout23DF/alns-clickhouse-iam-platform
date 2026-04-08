package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder(toBuilder = true)
public class ResultOperationOpenFgaDTO {
    private  int statusCode;
    private Map<String, List<String>> headers;
    private String rawResponse;
    private Exception occurredException;
    private String messageToLog = "";
    private Boolean checkingResult;
    private List<String> foundObjectsList;
}
