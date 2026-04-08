package com.clickhouse.alnscodingexercise.domains.shared.web.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class GenericOperationResponseDTO<TObjBody> {
    private int httpStatusCode;
    private TObjBody resultBody;
}
