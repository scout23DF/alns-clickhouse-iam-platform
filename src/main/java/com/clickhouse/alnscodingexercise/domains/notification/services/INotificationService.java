package com.clickhouse.alnscodingexercise.domains.notification.services;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.VerificationToken;
import com.clickhouse.alnscodingexercise.eventlisteners.events.OnRegistrationSubmittedEvent;

import java.util.Locale;

public interface INotificationService {

    void sendRegistrationConfirmationEmail(OnRegistrationSubmittedEvent onRegistrationSubmittedEvent, String generatedToken);

    void resendVerificationTokenEmailForRegistration(String appUrl, Locale locale, VerificationToken newToken, CHUserAccount CHUserAccount);

    void sendResetPasswordTokenEmail(String appUrl, Locale locale, String token, CHUserAccount CHUserAccount);

}
