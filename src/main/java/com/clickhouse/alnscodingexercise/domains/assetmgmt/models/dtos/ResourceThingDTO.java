package com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos;

import lombok.Builder;

@Builder(toBuilder = true)
public record ResourceThingDTO(
    String id,
    String title,
    String metadata,
    String summaryContent,
    String fullContent,
    String createdAt,
    String updatedAt,
    String creatorUsername
) {}
