package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos;

import lombok.Builder;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class GenericObjectEnrichedWithACLDTO<TObjProtectedItem> {

    private TObjProtectedItem itemProtected;
    List<ProtectedObjectAclDTO> protectedObjectsAclsList;

    public String getGrantedUiCrudAction() {
        String resultAction = "none";

        if (!CollectionUtils.isEmpty(protectedObjectsAclsList)) {

            if (protectedObjectsAclsList.getFirst().allowedActions().canWrite()) {
                resultAction = "edit";
            } else if (protectedObjectsAclsList.getFirst().allowedActions().canRead()) {
                resultAction = "view-only";
            }

        }

        return resultAction;
    }

}
