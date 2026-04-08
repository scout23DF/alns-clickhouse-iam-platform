package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos;

import lombok.Builder;

@Builder(toBuilder = true)
public record GenericResultOpenFgaDTO<T>(
    int httpStatusCode,
    T responseFromFga,
    Exception occurredException,
    String messageToLog
) {}
