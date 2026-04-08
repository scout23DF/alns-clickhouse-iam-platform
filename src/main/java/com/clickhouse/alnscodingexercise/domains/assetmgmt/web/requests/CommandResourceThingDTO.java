package com.clickhouse.alnscodingexercise.domains.assetmgmt.web.requests;

import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.AssignableGrantsRequestDTO;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record CommandResourceThingDTO(
    String id,
    String title,
    String metadata,
    String summaryContent,
    String fullContent,
    String creatorUsername,

    Boolean shouldIgnoreAclContent,

    List<AssignableGrantsRequestDTO> assignableGrantsRequestsList
) { }
