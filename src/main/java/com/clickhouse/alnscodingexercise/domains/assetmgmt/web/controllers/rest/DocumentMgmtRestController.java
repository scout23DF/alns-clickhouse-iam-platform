package com.clickhouse.alnscodingexercise.domains.assetmgmt.web.controllers.rest;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.DocumentDTO;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.services.IDocumentMgmtService;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppConstants.DEFAULT_API_ASSETSMGMT_PREFIX_PATH + "/documents")
@RequiredArgsConstructor
public class DocumentMgmtRestController {

    private final IDocumentMgmtService documentMgmtService;

    @PostMapping
    public String createDocument(@RequestBody DocumentDTO newDocumentDTO, String userId, String relationType) {
        return documentMgmtService.createDocument(newDocumentDTO, userId, relationType);
    }

}
