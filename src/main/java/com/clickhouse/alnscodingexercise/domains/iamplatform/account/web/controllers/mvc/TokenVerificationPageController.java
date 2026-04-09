package com.clickhouse.alnscodingexercise.domains.iamplatform.account.web.controllers.mvc;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.VerificationToken;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.services.IUserAccountMgmtService;
import com.clickhouse.alnscodingexercise.domains.notification.services.INotificationService;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import com.clickhouse.alnscodingexercise.domains.shared.web.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class TokenVerificationPageController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final MessageSource messages;
    private final IUserAccountMgmtService userAccountMgmtService;
    private final INotificationService notificationService;

    @GetMapping("/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/resendRegistrationToken")
    public String resendRegistrationToken(final HttpServletRequest request, final Model model, @RequestParam("token") final String existingToken) {
        final Locale locale = request.getLocale();
        final VerificationToken newToken = userAccountMgmtService.generateNewVerificationToken(existingToken);
        final CHUserAccount CHUserAccount = userAccountMgmtService.getUserByVerificationToken(newToken.getToken());
        try {
            this.notificationService.resendVerificationTokenEmailForRegistration(
                    RequestUtils.getAppUrl(request),
                    request.getLocale(),
                    newToken,
                    CHUserAccount
            );
        } catch (final MailAuthenticationException e) {
            LOGGER.debug("MailAuthenticationException", e);
            return "redirect:/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/emailError.html?lang=" + locale.getLanguage();
        } catch (final Exception e) {
            LOGGER.debug(e.getLocalizedMessage(), e);
            model.addAttribute("message", e.getLocalizedMessage());
            return "redirect:/" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/login-form.html?lang=" + locale.getLanguage();
        }
        model.addAttribute("message", messages.getMessage("message.resendToken", null, locale));
        return "redirect:/" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/login-form.html?lang=" + locale.getLanguage();
    }

    @GetMapping("/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/validateResetPasswordToken")
    public ModelAndView showChangePasswordPage(final ModelMap model,
                                               @RequestParam("token") final String token) {

        final String result = userAccountMgmtService.validatePasswordResetToken(token);

        if (result != null) {
            String messageKey = "auth.message." + result;
            model.addAttribute("messageKey", messageKey);
            return new ModelAndView("redirect:/" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/doLogin", model);
        } else {
            model.addAttribute("token", token);
            return new ModelAndView("redirect:/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/updatePassword");
        }
    }

}
