package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.DocumentDTO;

public interface IPermissionsMgmtService {

    DocumentDTO getDocumentWithPreAuthorize(String searchDocumentId, String searchUserId, String searchRelationType);

    DocumentDTO getDocumentWithPreOpenFgaCheck(String searchDocumentId, String searchUserId, String searchRelationType);

    DocumentDTO getDocumentWithPreReadDocumentCheck(String searchDocumentId);

    DocumentDTO findByContentWithPostOpenFgaCheck(String searchDocumentId, String searchRelationType, String content);

    DocumentDTO findByContentWithPostReadDocumentCheck(String content);

    DocumentDTO getDocumentWithFgaCheck(String searchDocumentId, String searchUserId, String searchRelationType);

}
