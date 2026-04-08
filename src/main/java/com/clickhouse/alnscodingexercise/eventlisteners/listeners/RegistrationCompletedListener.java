package com.clickhouse.alnscodingexercise.eventlisteners.listeners;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.repositories.UserRepository;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.PermissionSummaryDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaObjectTypeEnum;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.enums.FgaRelationEnum;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.IAuthorizationService;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import com.clickhouse.alnscodingexercise.eventlisteners.events.OnRegistrationCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegistrationCompletedListener implements ApplicationListener<OnRegistrationCompletedEvent> {

    private final UserRepository userRepository;
    private final IAuthorizationService authorizationService;

    @Override
    public void onApplicationEvent(@NonNull OnRegistrationCompletedEvent event) {
        this.applyPermissionsOnDefaultResources(event);
    }

    private void applyPermissionsOnDefaultResources(OnRegistrationCompletedEvent event) {
        CHUserAccount CHUserAccount = event.getChUserAccount();
        CHUserAccount.setEnabled(true);
        userRepository.saveAndFlush(CHUserAccount);

        authorizationService.addFGAPermissionsInBatch(List.of(
                PermissionSummaryDTO.builder()
                        .subjectType(FgaObjectTypeEnum.USER.toString())
                        .subjectId(CHUserAccount.getUsername())
                        .subjectShortDescription(CHUserAccount.getFirstName() + " " + CHUserAccount.getLastName())
                        .relationshipType(FgaRelationEnum.OWNER.toString())
                        .resourceType(FgaObjectTypeEnum.DOCUMENT.toString())
                        .resourceId(AppConstants.DEFAULT_RESOURCE_ID)
                        .resourceShortDescription(AppConstants.DEFAULT_RESOURCE_SHORT_DESCRIPTION)
                        .build()
                )
        );

    }

}
