package com.clickhouse.alnscodingexercise.eventlisteners.listeners;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.repositories.UserRepository;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.IAuthorizationService;
import com.clickhouse.alnscodingexercise.eventlisteners.events.OnResourceThingRemovedEvent;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceThingRemovedListener implements ApplicationListener<OnResourceThingRemovedEvent> {

    private final UserRepository userRepository;
    private final IAuthorizationService authorizationService;

    @Override
    public void onApplicationEvent(@NonNull OnResourceThingRemovedEvent event) {
        this.revokePermissionsAfterResourcesThingRemoval(event);
    }

    private void revokePermissionsAfterResourcesThingRemoval(OnResourceThingRemovedEvent event) {

        // TODO: Implement this
        /*
        CHUserAccount CHUserAccount = event.getChUserAccount();
        CHUserAccount.setEnabled(true);
        userRepository.saveAndFlush(CHUserAccount);

        authorizationService.addFGAPermission(
                PermissionSummaryDTO.builder()
                        .subjectType(FgaObjectTypeEnum.USER.toString())
                        .subjectId(CHUserAccount.getUsername())
                        .subjectShortDescription(CHUserAccount.getFirstName() + " " + CHUserAccount.getLastName())
                        .relationshipType(FgaRelationEnum.OWNER.toString())
                        .resourceType(FgaObjectTypeEnum.DOCUMENT.toString())
                        .resourceId(AppConstants.DEFAULT_RESOURCE_ID)
                        .resourceShortDescription(AppConstants.DEFAULT_RESOURCE_SHORT_DESCRIPTION)
                        .build()
        );
        */
    }

}
