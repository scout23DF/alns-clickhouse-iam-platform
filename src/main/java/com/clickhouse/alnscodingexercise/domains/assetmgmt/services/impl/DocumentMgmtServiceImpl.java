package com.clickhouse.alnscodingexercise.domains.assetmgmt.services.impl;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.DocumentDTO;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.services.IDocumentMgmtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentMgmtServiceImpl implements IDocumentMgmtService {

    @Override
    public String createDocument(DocumentDTO newDocument, String userId, String relationType) {
        return String.format("Created Document with ID: %s and associated user:%s as a '%s' FGA relationship",
                newDocument.id(), userId, relationType);
    }

}
