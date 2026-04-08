package com.clickhouse.alnscodingexercise.domains.iamplatform.account.web.controllers.mvc;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.PasswordResetToken;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.services.IUserAccountMgmtService;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.web.requests.PasswordRequestDTO;
import com.clickhouse.alnscodingexercise.domains.notification.services.INotificationService;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import com.clickhouse.alnscodingexercise.domains.shared.web.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CredentialPageController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final MessageSource messages;
    private final IUserAccountMgmtService userAccountMgmtService;
    private final INotificationService notificationService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/resetPassword")
    public String resetPassword(final HttpServletRequest request, final Model model, @RequestParam("email") final String userEmail) {
        final CHUserAccount CHUserAccount = userAccountMgmtService.findUserByEmail(userEmail);
        if (CHUserAccount == null) {
            model.addAttribute("message", messages.getMessage("message.userNotFound", null, request.getLocale()));
            return "redirect:/" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/login-form.html?lang=" + request.getLocale().getLanguage();
        }

        final String token = UUID.randomUUID().toString();
        userAccountMgmtService.createPasswordResetTokenForUser(CHUserAccount, token);

        try {

            this.notificationService.sendResetPasswordTokenEmail(
                    RequestUtils.getAppUrl(request),
                    request.getLocale(),
                    token,
                    CHUserAccount
            );

        } catch (MailAuthenticationException e) {
            LOGGER.debug("MailAuthenticationException", e);
            return "redirect:/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/emailError.html?lang=" + request.getLocale().getLanguage();
        } catch (final Exception e) {
            LOGGER.debug(e.getLocalizedMessage(), e);
            model.addAttribute("message", e.getLocalizedMessage());
            return "redirect:/" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/login-form.html?lang=" + request.getLocale().getLanguage();
        }
        model.addAttribute("message", messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
        return "redirect:/" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/login-form.html?lang=" + request.getLocale().getLanguage();
    }

    @PostMapping("/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/savePassword")
    @PreAuthorize("hasRole('READ_PRIVILEGE')")
    public String savePassword(final HttpServletRequest request, final Model model, @RequestParam("password") final String password) {
        final Locale locale = request.getLocale();

        final CHUserAccount CHUserAccount = (CHUserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PasswordRequestDTO passwordRequestDTO = PasswordRequestDTO.builder()
                .newPassword(password)
                .build();
        userAccountMgmtService.changeUserPassword(CHUserAccount, passwordRequestDTO);
        model.addAttribute("message", messages.getMessage("message.resetPasswordSuc", null, locale));
        return "redirect:/" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/login-form.html?lang=" + locale;
    }

    @GetMapping("/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/updatePassword")
    public ModelAndView updatePassword(final HttpServletRequest request, final ModelMap model, @RequestParam("messageKey" ) final Optional<String> messageKey) {
        Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());
        messageKey.ifPresent( key -> {
                    String message = messages.getMessage(key, null, locale);
                    model.addAttribute("message", message);
                }
        );

        return new ModelAndView(AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/update-password-form", model);
    }

    @GetMapping("/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/changePassword")
    public String changePassword(HttpServletRequest request,
                                 Model model,
                                 @RequestParam("id") final long id,
                                 @RequestParam("token") final String token) {

        Locale locale = request.getLocale();

        PasswordResetToken passToken = userAccountMgmtService.getPasswordResetToken(token);

        if (passToken == null || passToken.getUser().getId() != id) {
            String message = messages.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message", message);
            return "redirect:/" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/login-form.html?lang=" + locale.getLanguage();
        }

        Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            model.addAttribute("message", messages.getMessage("auth.message.expired", null, locale));
            return "redirect:/" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/login-form.html?lang=" + locale.getLanguage();
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(
                passToken.getUser(),
                null,
                userDetailsService.loadUserByUsername(passToken.getUser().getEmail()).getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        return "redirect:/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/update-password-form.html?lang=" + locale.getLanguage();
    }


}
