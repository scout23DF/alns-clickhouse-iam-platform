package com.clickhouse.alnscodingexercise.domains.assetmgmt.web.controllers.mvc;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.ResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.services.IResourceThingMgmtService;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.web.requests.CommandResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import com.clickhouse.alnscodingexercise.domains.shared.web.utils.RequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ResourceThingMgmtPageController {

    private final IResourceThingMgmtService resourceThingMgmtService;
    private final RequestUtils requestUtils;

    @GetMapping("/" + AppConstants.DEFAULT_PAGES_ASSETSMGMT_PREFIX_PATH + "/resources-things" + "/edit/{resourceThingId}")
    @PreAuthorize("@authorizationUtils.checkFGAPermission(#resourceThingId, 'document', 'can_write', 'user', authentication?.name)")
    public String showUpdateForm(@PathVariable String resourceThingId, Model model) {

        ResourceThingDTO foundResourceThing = resourceThingMgmtService.searchByContainingText(
                resourceThingId,
                "id")
                .stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Any ResourceThing found for Id:" + resourceThingId));

        model.addAttribute("targetResourceThing", foundResourceThing);
        model.addAttribute("authenticatedUser", requestUtils.getCurrentAuthenticatedUser());

        return AppConstants.DEFAULT_PAGES_ASSETSMGMT_PREFIX_PATH + "/resource-thing-edit-form";

    }

    @PostMapping("/" + AppConstants.DEFAULT_PAGES_ASSETSMGMT_PREFIX_PATH + "/resources-things" + "/update/{resourceThingId}")
    @PreAuthorize("@authorizationUtils.checkFGAPermission(#resourceThingId, 'document', 'can_write', 'user', authentication?.name)")
    public String updateItem(@PathVariable String resourceThingId, ResourceThingDTO targetResourceThing) {

        ResourceThingDTO updatedResourceThing = resourceThingMgmtService.updateResourceThing(
                CommandResourceThingDTO.builder()
                        .id(resourceThingId)
                        .title(targetResourceThing.title())
                        .metadata(targetResourceThing.metadata())
                        .summaryContent(targetResourceThing.summaryContent())
                        .fullContent(targetResourceThing.fullContent())
                        .creatorUsername(requestUtils.getCurrentAuthenticatedUser().getUsername())
                        .shouldIgnoreAclContent(true)
                        .build()
        );

        return "redirect:/" + AppConstants.DEFAULT_PAGES_DASHBOARD_PREFIX_PATH + "/handlePortalHome";
    }

    @GetMapping("/" + AppConstants.DEFAULT_PAGES_ASSETSMGMT_PREFIX_PATH + "/resources-things" + "/view/{resourceThingId}")
    @PreAuthorize("@authorizationUtils.checkFGAPermission(#resourceThingId, 'document', 'can_read', 'user', authentication?.name)")
    public String showViewForm(@PathVariable String resourceThingId, Model model) {

        ResourceThingDTO foundResourceThing = resourceThingMgmtService.searchByContainingText(
                        resourceThingId,
                        "id")
                .stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Any ResourceThing found for Id:" + resourceThingId));

        model.addAttribute("targetResourceThing", foundResourceThing);
        model.addAttribute("authenticatedUser", requestUtils.getCurrentAuthenticatedUser());

        return AppConstants.DEFAULT_PAGES_ASSETSMGMT_PREFIX_PATH + "/resource-thing-view-form";

    }
}
