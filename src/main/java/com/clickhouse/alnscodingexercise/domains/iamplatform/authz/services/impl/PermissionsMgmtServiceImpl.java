package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.impl;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.DocumentDTO;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.services.IDocumentMgmtService;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.IPermissionsMgmtService;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.validation.annotations.*;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientTupleKey;
import dev.openfga.sdk.api.client.model.ClientWriteRequest;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class PermissionsMgmtServiceImpl implements IPermissionsMgmtService {

    @Override
    @PreAuthorize("@authorizationUtils.checkFGAPermission(#searchDocumentId, 'document', #searchRelationType, 'user', authentication?.name)")
    public DocumentDTO getDocumentWithPreAuthorize(String searchDocumentId, String searchUserId, String searchRelationType) {
        return new DocumentDTO(
                searchDocumentId,
                "[Document Created in runtime - The Owner : UserId = " + searchUserId + "]: You have reader access to this document! (Relation Type = " + searchRelationType + ")"
        );
    }

    @Override
    @PreOpenFgaCheck(userType="'user'", relation="#searchRelationType", objectType="'document'", object="#searchDocumentId")
    public DocumentDTO getDocumentWithPreOpenFgaCheck(String searchDocumentId, String searchUserId, String searchRelationType) {
        return new DocumentDTO(
                searchDocumentId,
                "[Document Created in runtime - The Owner : UserId = " + searchUserId + "]: You have reader access to this document! (Relation Type = " + searchRelationType + ")"
        );
    }

    @Override
    @PreReadDocumentCheck("#searchDocumentId")
    public DocumentDTO getDocumentWithPreReadDocumentCheck(String searchDocumentId) {
        return new DocumentDTO(searchDocumentId, "You have reader access to this document");
    }

    @Override
    @PostOpenFgaCheck(userType="'user'", relation="#searchRelationType", objectType="'document'", object="returnObject.id")
    public DocumentDTO findByContentWithPostOpenFgaCheck(String searchDocumentId, String searchRelationType, String content) {
        return new DocumentDTO(
                searchDocumentId,
                "[Document Created in runtime - The Owner : UserId = (logged User)]: Found the content here: " + content + " :: (Relation Type = " + searchRelationType + ")"
        );
    }

    @Override
    @PostReadDocumentCheck("returnObject.id")
    public DocumentDTO findByContentWithPostReadDocumentCheck(String content) {
        return new DocumentDTO("1", "Found the content here: '" + content + "'");
    }

    @Override
    @FgaCheck(userType="user", userId="#searchUserId", relation="#searchRelationType", objectType="document", object="#searchDocumentId")
    public DocumentDTO getDocumentWithFgaCheck(String searchDocumentId, String searchUserId, String searchRelationType) {
        return new DocumentDTO(
                searchDocumentId,
                "[Document Created in runtime - The Owner : UserId = " + searchUserId + "]: You have reader access to this document! (Relation Type = " + searchRelationType + ")"
        );
    }

}
