package com.clickhouse.alnscodingexercise.domains.assetmgmt.web.controllers.mvc;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.ResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.services.IResourceThingMgmtService;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.web.requests.CommandResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import com.clickhouse.alnscodingexercise.domains.shared.web.utils.RequestUtils;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/" + AppConstants.DEFAULT_PAGES_ASSETSMGMT_PREFIX_PATH + "/resources-things" + "/edit/{id}")
    public String showUpdateForm(@PathVariable String id, Model model) {

        ResourceThingDTO foundResourceThing = resourceThingMgmtService.searchByContainingText(
                id,
                "id")
                .stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Any ResourceThing found for Id:" + id));

        model.addAttribute("targetResourceThing", foundResourceThing);
        model.addAttribute("authenticatedUser", requestUtils.getCurrentAuthenticatedUser());

        return AppConstants.DEFAULT_PAGES_ASSETSMGMT_PREFIX_PATH + "/resource-thing-edit-form";

    }

    @PostMapping("/" + AppConstants.DEFAULT_PAGES_ASSETSMGMT_PREFIX_PATH + "/resources-things" + "/update/{id}")
    public String updateItem(@PathVariable String id, ResourceThingDTO targetResourceThing) {

        ResourceThingDTO updatedResourceThing = resourceThingMgmtService.updateResourceThing(
                CommandResourceThingDTO.builder()
                        .id(id)
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

    @GetMapping("/" + AppConstants.DEFAULT_PAGES_ASSETSMGMT_PREFIX_PATH + "/resources-things" + "/view/{id}")
    public String showViewForm(@PathVariable String id, Model model) {

        ResourceThingDTO foundResourceThing = resourceThingMgmtService.searchByContainingText(
                        id,
                        "id")
                .stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Any ResourceThing found for Id:" + id));

        model.addAttribute("targetResourceThing", foundResourceThing);
        model.addAttribute("authenticatedUser", requestUtils.getCurrentAuthenticatedUser());

        return AppConstants.DEFAULT_PAGES_ASSETSMGMT_PREFIX_PATH + "/resource-thing-view-form";

    }

    /*
    @GetMapping("/")
    public String listItems(Model model) {
        model.addAttribute("items", itemRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String showAddForm(Item item) {
        return "add-item";
    }

    @PostMapping("/add")
    public String addItem(Item item) {
        itemRepository.save(item);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable("id") long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));
        itemRepository.delete(item);
        return "redirect:/";
    }
    */
}
