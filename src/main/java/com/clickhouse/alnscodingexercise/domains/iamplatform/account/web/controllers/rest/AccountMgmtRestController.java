package com.clickhouse.alnscodingexercise.domains.iamplatform.account.web.controllers.rest;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.VerificationToken;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.services.IUserAccountMgmtService;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.web.requests.CreateUserAccountRequestDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.web.requests.PasswordRequestDTO;
import com.clickhouse.alnscodingexercise.domains.notification.services.INotificationService;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import com.clickhouse.alnscodingexercise.domains.shared.exceptions.InvalidOldPasswordException;
import com.clickhouse.alnscodingexercise.domains.shared.web.dtos.GenericErrorResponseDTO;
import com.clickhouse.alnscodingexercise.domains.shared.web.utils.RequestUtils;
import com.clickhouse.alnscodingexercise.eventlisteners.events.OnRegistrationSubmittedEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AccountMgmtRestController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final MessageSource messages;
    private final IUserAccountMgmtService userAccountMgmtService;
    private final ApplicationEventPublisher eventPublisher;
    private final INotificationService notificationService;

    @PostMapping(AppConstants.DEFAULT_API_IAM_ACCOUNT_PREFIX_PATH + "/registration")
    public GenericErrorResponseDTO registerUserAccountRest(@Valid CreateUserAccountRequestDTO accountDto,
                                                           HttpServletRequest request) {

        LOGGER.debug("Registering user account with information: {}", accountDto);

        CHUserAccount registered = userAccountMgmtService.registerNewUserAccount(accountDto, request.getLocale(), RequestUtils.getAppUrl(request));

        eventPublisher.publishEvent(
                new OnRegistrationSubmittedEvent(registered, request.getLocale(), RequestUtils.getAppUrl(request))
        );

        return new GenericErrorResponseDTO("success");
    }

    @PostMapping(AppConstants.DEFAULT_API_IAM_ACCOUNT_PREFIX_PATH + "/resetPassword")
    public GenericErrorResponseDTO resetPasswordRest(HttpServletRequest request, @RequestParam("email") String userEmail) {
        final CHUserAccount CHUserAccount = userAccountMgmtService.getUserByEmail(userEmail);
        if (CHUserAccount != null) {
            String token = UUID.randomUUID().toString();
            userAccountMgmtService.createPasswordResetTokenForUser(CHUserAccount, token);

            this.notificationService.sendResetPasswordTokenEmail(
                    RequestUtils.getAppUrl(request),
                    request.getLocale(),
                    token,
                    CHUserAccount
            );

        }
        return new GenericErrorResponseDTO(messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
    }

    // Save password
    @PostMapping(AppConstants.DEFAULT_API_IAM_ACCOUNT_PREFIX_PATH + "/savePassword")
    public GenericErrorResponseDTO savePasswordRest(Locale locale, @Valid PasswordRequestDTO passwordRequestDTO) {

        String result = userAccountMgmtService.validatePasswordResetToken(passwordRequestDTO.getToken());

        if (result != null) {
            return new GenericErrorResponseDTO(messages.getMessage("auth.message." + result, null, locale));
        }

        Optional<CHUserAccount> user = userAccountMgmtService.getUserByPasswordResetToken(passwordRequestDTO.getToken());
        if (user.isPresent()) {
            userAccountMgmtService.changeUserPassword(user.get(), passwordRequestDTO);
            return new GenericErrorResponseDTO(messages.getMessage("message.resetPasswordSuc", null, locale));
        } else {
            return new GenericErrorResponseDTO(messages.getMessage("auth.message.invalid", null, locale));
        }
    }

    // Change user password
    @PostMapping(AppConstants.DEFAULT_API_IAM_ACCOUNT_PREFIX_PATH + "/updatePassword")
    public GenericErrorResponseDTO updatePasswordRest(Locale locale, @Valid PasswordRequestDTO passwordRequestDTO) {
        final CHUserAccount CHUserAccount = userAccountMgmtService.getUserByEmail(((CHUserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail());
        if (!userAccountMgmtService.checkIfValidOldPassword(CHUserAccount, passwordRequestDTO.getOldPassword())) {
            throw new InvalidOldPasswordException();
        }
        userAccountMgmtService.changeUserPassword(CHUserAccount, passwordRequestDTO);
        return new GenericErrorResponseDTO(messages.getMessage("message.updatePasswordSuc", null, locale));
    }

    @GetMapping(AppConstants.DEFAULT_API_IAM_ACCOUNT_PREFIX_PATH + "/resendRegistrationToken")
    public GenericErrorResponseDTO resendRegistrationTokenRest(HttpServletRequest request, @RequestParam("token") String existingToken) {
        VerificationToken newToken = userAccountMgmtService.generateNewVerificationToken(existingToken);
        CHUserAccount CHUserAccount = userAccountMgmtService.getUserByVerificationToken(newToken.getToken());

        this.notificationService.resendVerificationTokenEmailForRegistration(
                RequestUtils.getAppUrl(request),
                request.getLocale(),
                newToken,
                CHUserAccount
        );

        return new GenericErrorResponseDTO(messages.getMessage("message.resendToken", null, request.getLocale()));
    }

}
