package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class GenericObjectEnrichedWithACLDTO<TObjProtectedItem> {

    private TObjProtectedItem itemProtected;
    List<ProtectedObjectAclDTO> protectedObjectsAclsList;

}
