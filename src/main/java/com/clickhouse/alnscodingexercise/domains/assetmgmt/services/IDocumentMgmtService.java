package com.clickhouse.alnscodingexercise.domains.assetmgmt.services;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.DocumentDTO;

public interface IDocumentMgmtService {

    String createDocument(DocumentDTO newDocument, String userId, String relationType);

}
