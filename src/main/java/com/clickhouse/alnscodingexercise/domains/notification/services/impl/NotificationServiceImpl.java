package com.clickhouse.alnscodingexercise.domains.notification.services.impl;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.VerificationToken;
import com.clickhouse.alnscodingexercise.domains.notification.services.INotificationService;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import com.clickhouse.alnscodingexercise.eventlisteners.events.OnRegistrationSubmittedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final MessageSource messages;
    private final JavaMailSender mailSender;
    private final Environment env;

    @Override
    public void sendRegistrationConfirmationEmail(OnRegistrationSubmittedEvent onRegistrationSubmittedEvent,
                                                  String generatedToken) {

        String subject = "Registration Confirmation";
        String confirmationUrl = onRegistrationSubmittedEvent.getAppUrl() +
                "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH +
                "/registrationConfirm?token=" + generatedToken;
        String emailBodyContent = messages.getMessage(
                "message.regSuccLink",
                null,
                "You registered successfully. To confirm your registration, please click on the below link.",
                onRegistrationSubmittedEvent.getLocale()
        );

        emailBodyContent += "\n \n \t Click Here to confirm your new Account: " + confirmationUrl;

        SimpleMailMessage emailMessage = constructEmail(subject, emailBodyContent, onRegistrationSubmittedEvent.getChUserAccount());

        mailSender.send(emailMessage);
    }

    @Override
    public void resendVerificationTokenEmailForRegistration(String appUrl, Locale locale, VerificationToken newToken, CHUserAccount CHUserAccount) {
        final SimpleMailMessage email = constructResendVerificationTokenEmail(appUrl, locale, newToken, CHUserAccount);
        mailSender.send(email);
    }

    @Override
    public void sendResetPasswordTokenEmail(String appUrl, Locale locale, String token, CHUserAccount CHUserAccount) {
        final SimpleMailMessage email = constructResetTokenEmail(appUrl, locale, token, CHUserAccount);
        mailSender.send(email);
    }

    public SimpleMailMessage constructResendVerificationTokenEmail(final String contextPath, final Locale locale, final VerificationToken newToken, final CHUserAccount CHUserAccount) {
        final String confirmationUrl = contextPath +
                "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH +
                "/registrationConfirm.html?token=" + newToken.getToken();
        final String message = messages.getMessage("message.resendToken", null, locale);
        return constructEmail("Resend Registration Token", message + " \r\n" + confirmationUrl, CHUserAccount);

    }

    public SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale, final String token, final CHUserAccount CHUserAccount) {
        String url = contextPath +
                "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH +
                "/validateResetPasswordToken?token=" + token;
        String message = messages.getMessage("message.resetPassword", null, locale);

        message += "\r\n\n\t - Your Token value = " + token + " (for testing purposes)";

        return constructEmail("Reset Password", message + " \r\n" + url, CHUserAccount);

        /*
        final String url = contextPath + "/old/user/changePassword?id=" + user.getId() + "&token=" + token;
        final String message = messages.getMessage("message.resetPassword", null, locale);
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Reset Password");
        email.setText(message + " \r\n" + url);
        email.setFrom(env.getProperty("support.email"));
        return email;
        */

    }

    public SimpleMailMessage constructEmail(String subject, String body, CHUserAccount CHUserAccount) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(CHUserAccount.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

}
