package com.clickhouse.alnscodingexercise.eventlisteners.listeners;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.repositories.UserRepository;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.PermissionSummaryDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaObjectTypeEnum;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.IAuthorizationService;
import com.clickhouse.alnscodingexercise.eventlisteners.events.OnResourceThingCreatedOrUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ResourceThingCreatedOrUpdatedListener implements ApplicationListener<OnResourceThingCreatedOrUpdatedEvent> {

    private final UserRepository userRepository;
    private final IAuthorizationService authorizationService;

    @Override
    public void onApplicationEvent(@NonNull OnResourceThingCreatedOrUpdatedEvent event) {
        this.applyPermissionsFromACL(event);
    }

    private void applyPermissionsFromACL(OnResourceThingCreatedOrUpdatedEvent event) {

        if (!CollectionUtils.isEmpty(event.getAccessControlList())) {

            event.getAccessControlList().forEach(oneAcl -> {
               if (!CollectionUtils.isEmpty(oneAcl.rolesNamesRelationsList())) {

                   oneAcl.rolesNamesRelationsList().forEach(oneRole -> {

                       doAddFgaPermission(
                               oneAcl.subjectId(),
                               oneRole,
                               event.getResourceThingDTO().id(),
                               "editor"
                       );

                   });

               }

               /*
               if (oneAcl.allowedActions().canChangeOwner()) {
                   doAddFgaPermission(
                           oneAcl.subjectId(),
                           FgaRelationEnum.CAN_CHANGE_OWNER.toString(),
                           event.getResourceThingDTO().id()
                   );
               }

                if (oneAcl.allowedActions().canDelete()) {
                    doAddFgaPermission(
                            oneAcl.subjectId(),
                            FgaRelationEnum.CAN_DELETE.toString(),
                            event.getResourceThingDTO().id()
                    );
                }

                if (oneAcl.allowedActions().canRead()) {
                    doAddFgaPermission(
                            oneAcl.subjectId(),
                            FgaRelationEnum.CAN_READ.toString(),
                            event.getResourceThingDTO().id()
                    );
                }

                if (oneAcl.allowedActions().canWrite()) {
                    doAddFgaPermission(
                            oneAcl.subjectId(),
                            FgaRelationEnum.CAN_WRITE.toString(),
                            event.getResourceThingDTO().id()
                    );
                }

                if (oneAcl.allowedActions().canShare()) {
                    doAddFgaPermission(
                            oneAcl.subjectId(),
                            FgaRelationEnum.CAN_SHARE.toString(),
                            event.getResourceThingDTO().id()
                    );
                }
                */
            });

        }

    }

    private void doAddFgaPermission(String userId,
                                    String relationshipType,
                                    String resourceThingId,
                                    String resourceRelationshipType) {

        authorizationService.addFGAPermissionsInBatch(List.of(
                PermissionSummaryDTO.builder()
                        .subjectType(FgaObjectTypeEnum.USER.toString())
                        .subjectId(userId)
                        .relationshipType(relationshipType.toLowerCase())
                        .resourceType(FgaObjectTypeEnum.DOCUMENT.toString())
                        .resourceId(resourceThingId)
                        .resourceRelationshipType((Objects.nonNull(resourceRelationshipType) ? resourceRelationshipType.toLowerCase() : null))
                        .build()

            )
        );

    }

}
