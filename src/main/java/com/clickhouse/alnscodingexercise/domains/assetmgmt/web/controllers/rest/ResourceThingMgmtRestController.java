package com.clickhouse.alnscodingexercise.domains.assetmgmt.web.controllers.rest;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.ResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.services.IResourceThingMgmtService;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.web.requests.CommandResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.GenericObjectEnrichedWithACLDTO;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import com.clickhouse.alnscodingexercise.domains.shared.web.dtos.GenericOperationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppConstants.DEFAULT_API_ASSETSMGMT_PREFIX_PATH + "/resources-things")
@RequiredArgsConstructor
public class ResourceThingMgmtRestController {

    private final IResourceThingMgmtService resourceThingMgmtService;

    @PostMapping
    public ResponseEntity<GenericOperationResponseDTO<ResourceThingDTO>> createResourceThing(
            @RequestBody CommandResourceThingDTO commandResourceThingDTO
    ) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                GenericOperationResponseDTO.<ResourceThingDTO>builder()
                    .httpStatusCode(HttpStatus.CREATED.value())
                    .resultBody(resourceThingMgmtService.createResourceThing(commandResourceThingDTO))
                .build()
        );

    }

    @PostMapping("/batch")
    public ResponseEntity<GenericOperationResponseDTO<List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>>>> createResourcesThingsList(
            @RequestBody List<CommandResourceThingDTO> commandsResourceThingsDTOList
    ) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                GenericOperationResponseDTO.<List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>>>builder()
                    .httpStatusCode(HttpStatus.CREATED.value())
                    .resultBody(resourceThingMgmtService.createResourcesThingsList(commandsResourceThingsDTOList))
                .build()
        );

    }

    @GetMapping("/search-by-containing-text")
    public ResponseEntity<GenericOperationResponseDTO<List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>>>> searchByContainingTextWithACL(
            @RequestParam String propertyNameToSearch,
            @RequestParam String propertyValueToSearch,
            @RequestParam List<String> usersIdsToFilterList
    ) {

        List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>> foundResourceThingsList = resourceThingMgmtService.searchByContainingTextWithACL(
                propertyValueToSearch,
                propertyNameToSearch,
                usersIdsToFilterList
        );

        return ResponseEntity.status(HttpStatus.OK).body(
                GenericOperationResponseDTO.<List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>>>builder()
                    .httpStatusCode(HttpStatus.OK.value())
                    .resultBody(foundResourceThingsList)
                .build()
        );

    }

}
