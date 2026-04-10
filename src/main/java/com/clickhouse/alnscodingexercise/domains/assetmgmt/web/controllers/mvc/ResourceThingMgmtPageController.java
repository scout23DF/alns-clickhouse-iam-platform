package com.clickhouse.alnscodingexercise.domains.assetmgmt.web.controllers.mvc;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.ResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.services.IResourceThingMgmtService;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.web.requests.CommandResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.GenericObjectEnrichedWithACLDTO;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import com.clickhouse.alnscodingexercise.domains.shared.models.enums.GenericOperationResultEnum;
import com.clickhouse.alnscodingexercise.domains.shared.web.dtos.GenericOperationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(AppConstants.DEFAULT_API_ASSETSMGMT_PREFIX_PATH + "/resources-things")
@RequiredArgsConstructor
public class ResourceThingMgmtPageController {

    private final IResourceThingMgmtService resourceThingMgmtService;

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

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));
        model.addAttribute("item", item);
        return "update-item";
    }

    @PostMapping("/update/{id}")
    public String updateItem(@PathVariable("id") long id, Item item) {
        item.setId(id);
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
