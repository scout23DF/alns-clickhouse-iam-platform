package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Data
@Builder(toBuilder = true)
public class GenericObjectEnrichedWithACLDTO<TObjProtectedItem> {

    private TObjProtectedItem itemProtected;
    List<ProtectedObjectAclDTO> protectedObjectsAclsList;

    @JsonIgnore
    public String getGrantedUiCrudAction() {
        String resultAction = "none";

        if (!CollectionUtils.isEmpty(protectedObjectsAclsList)) {

            ProtectedObjectAclDTO firstAcl = protectedObjectsAclsList.getFirst();
            AllowedActionsDTO allowedActions = firstAcl != null ? firstAcl.allowedActions() : null;

            if (allowedActions != null && Objects.equals(Boolean.TRUE, allowedActions.canWrite())) {
                resultAction = "edit";
            } else if (allowedActions != null && Objects.equals(Boolean.TRUE, allowedActions.canRead())) {
                resultAction = "view-only";
            }

        }

        return resultAction;
    }

}
