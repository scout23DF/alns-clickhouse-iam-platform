package com.clickhouse.alnscodingexercise.domains.assetmgmt.web.controllers.rest;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.ResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.services.IResourceThingMgmtService;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.web.requests.CommandResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.GenericObjectEnrichedWithACLDTO;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import com.clickhouse.alnscodingexercise.domains.shared.models.enums.GenericOperationResultEnum;
import com.clickhouse.alnscodingexercise.domains.shared.web.dtos.GenericOperationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppConstants.DEFAULT_API_ASSETSMGMT_PREFIX_PATH + "/resources-things")
@RequiredArgsConstructor
public class ResourceThingMgmtRestController {

    private final IResourceThingMgmtService resourceThingMgmtService;

    @PostMapping
    public GenericOperationResponseDTO<ResourceThingDTO> createResourceThing(
            @RequestBody CommandResourceThingDTO commandResourceThingDTO
    ) {

        return GenericOperationResponseDTO.<ResourceThingDTO>builder()
                .httpStatusCode(HttpStatus.CREATED.value())
                .resultBody(resourceThingMgmtService.createResourceThing(commandResourceThingDTO))
                .build();

    }

    @PostMapping("/as-list")
    public GenericOperationResponseDTO<List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>>> createResourcesThingsList(
            @RequestBody List<CommandResourceThingDTO> commandsResourceThingsDTOList
    ) {

        return GenericOperationResponseDTO.<List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>>>builder()
                .httpStatusCode(HttpStatus.CREATED.value())
                .resultBody(resourceThingMgmtService.createResourcesThingsList(commandsResourceThingsDTOList))
                .build();

    }

    @PutMapping
    public GenericOperationResponseDTO<ResourceThingDTO> updateResourceThing(@RequestBody CommandResourceThingDTO commandResourceThingDTO) {

        ResourceThingDTO newResourceThing = resourceThingMgmtService.createResourceThing(commandResourceThingDTO);

        return GenericOperationResponseDTO.<ResourceThingDTO>builder()
                .httpStatusCode(HttpStatus.ACCEPTED.value())
                .resultBody(newResourceThing)
                .build();

    }

    @DeleteMapping
    public GenericOperationResponseDTO<List<GenericOperationResultEnum>> removeResourceThing(@RequestBody List<String> idsToRemoveList) {

        List<GenericOperationResultEnum> operationResultsList = resourceThingMgmtService.removeResourceThingsByIds(idsToRemoveList);

        return GenericOperationResponseDTO.<List<GenericOperationResultEnum>>builder()
                .httpStatusCode(HttpStatus.NO_CONTENT.value())
                .resultBody(operationResultsList)
                .build();

    }

    @GetMapping("/{resourceId}")
    public GenericOperationResponseDTO<ResourceThingDTO> searchById(@PathVariable String resourceId) {

        ResourceThingDTO foundResourceThing = resourceThingMgmtService.searchByContainingText(resourceId, "id")
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ResourceThing with id: " + resourceId + " not found"));

        return GenericOperationResponseDTO.<ResourceThingDTO>builder()
                .httpStatusCode(HttpStatus.OK.value())
                .resultBody(foundResourceThing)
                .build();

    }

    @GetMapping("/search-by-containing-text")
    public GenericOperationResponseDTO<List<ResourceThingDTO>> searchByContainingText(
            @RequestParam String propertyNameToSearch,
            @RequestParam String propertyValueToSearch
    ) {

        List<ResourceThingDTO> foundResourceThingsList = resourceThingMgmtService.searchByContainingText(propertyValueToSearch, propertyNameToSearch);

        return GenericOperationResponseDTO.<List<ResourceThingDTO>>builder()
                .httpStatusCode(HttpStatus.OK.value())
                .resultBody(foundResourceThingsList)
                .build();

    }

    @GetMapping("/search-by-containing-text-with-acl")
    public GenericOperationResponseDTO<List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>>> searchByContainingTextWithACL(
            @RequestParam String propertyNameToSearch,
            @RequestParam String propertyValueToSearch,
            @RequestParam List<String> usersIdsToFilterList
    ) {

        List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>> foundResourceThingsList = resourceThingMgmtService.searchByContainingTextWithACL(
                propertyValueToSearch,
                propertyNameToSearch,
                usersIdsToFilterList
        );

        return GenericOperationResponseDTO.<List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>>>builder()
                .httpStatusCode(HttpStatus.OK.value())
                .resultBody(foundResourceThingsList)
                .build();

    }

}
