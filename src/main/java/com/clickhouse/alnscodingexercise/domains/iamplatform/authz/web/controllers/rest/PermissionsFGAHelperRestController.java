package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.web.controllers.rest;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.DocumentDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.IPermissionsMgmtService;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppConstants.DEFAULT_API_IAM_AUTHZ_PREFIX_PATH + "/permissions-helper")
@RequiredArgsConstructor
public class PermissionsFGAHelperRestController {

    private final IPermissionsMgmtService permissionsMgmtService;

    @GetMapping("/via-authz-annotations/{documentId}")
    public DocumentDTO searchDocumentByIdViaAuthorizeAnnotations(
            @PathVariable String documentId,
            @RequestParam String searchUserId,
            @RequestParam String searchRelationType
    ) {
        return permissionsMgmtService.getDocumentWithPreAuthorize(documentId, searchUserId, searchRelationType);
    }

    @GetMapping("/via-aspectj/{documentId}")
    public DocumentDTO searchDocumentByIdViaAspectJPointcut(
            @PathVariable String documentId,
            @RequestParam String searchUserId,
            @RequestParam String searchRelationType
    ) {
        return permissionsMgmtService.getDocumentWithFgaCheck(documentId, searchUserId, searchRelationType);
    }

}
