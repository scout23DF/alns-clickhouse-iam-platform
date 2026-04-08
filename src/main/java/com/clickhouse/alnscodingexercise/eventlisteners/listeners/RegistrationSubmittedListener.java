package com.clickhouse.alnscodingexercise.eventlisteners.listeners;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.services.IUserAccountMgmtService;
import com.clickhouse.alnscodingexercise.domains.notification.services.INotificationService;
import com.clickhouse.alnscodingexercise.eventlisteners.events.OnRegistrationSubmittedEvent;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrationSubmittedListener implements ApplicationListener<OnRegistrationSubmittedEvent> {

    private final IUserAccountMgmtService userAccountMgmtService;
    private final INotificationService notificationService;

    @Override
    public void onApplicationEvent(final @NonNull OnRegistrationSubmittedEvent event) {
        this.generateAndSendVerificationToken(event);
    }

    private void generateAndSendVerificationToken(final OnRegistrationSubmittedEvent event) {
        CHUserAccount CHUserAccount = event.getChUserAccount();
        String token = UUID.randomUUID().toString();

        userAccountMgmtService.createVerificationTokenForUser(CHUserAccount, token);

        notificationService.sendRegistrationConfirmationEmail(event, token);

    }

}
