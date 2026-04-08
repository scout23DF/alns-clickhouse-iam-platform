package com.clickhouse.alnscodingexercise.domains.iamplatform.account.web.controllers.mvc;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.*;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.services.IUserAccountMgmtService;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.web.requests.CreateUserAccountRequestDTO;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import com.clickhouse.alnscodingexercise.domains.shared.exceptions.UserAlreadyExistException;
import com.clickhouse.alnscodingexercise.domains.shared.web.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class RegistrationPageController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final MessageSource messages;
    private final IUserAccountMgmtService userAccountMgmtService;

    @GetMapping("/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/registration")
    public String showRegistrationForm(HttpServletRequest request, Model model) {
        LOGGER.debug("Rendering Registration Form page.");
        final CreateUserAccountRequestDTO accountDto = new CreateUserAccountRequestDTO();
        model.addAttribute("user", accountDto);
        return AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/registration";
    }

    @PostMapping("/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/registration")
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid CreateUserAccountRequestDTO createUserAccountRequestDto,
                                            HttpServletRequest request,
                                            Errors errors) {
        LOGGER.debug("Registering user account with information: {}", createUserAccountRequestDto);

        try {

            final CHUserAccount registered = userAccountMgmtService.registerNewUserAccount(
                    createUserAccountRequestDto,
                    request.getLocale(),
                    RequestUtils.getAppUrl(request)
            );

        } catch (UserAlreadyExistException uaeEx) {
            ModelAndView mav = new ModelAndView("registration", "user", createUserAccountRequestDto);
            String errMessage = messages.getMessage("message.regError", null, request.getLocale());
            mav.addObject("message", errMessage);
            return mav;
        } catch (RuntimeException ex) {
            LOGGER.warn("Unable to register user", ex);
            return new ModelAndView(AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/emailError", "user", createUserAccountRequestDto);
        }
        return new ModelAndView(AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/successRegister", "user", createUserAccountRequestDto);
    }

    @GetMapping("/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/registrationConfirm")
    public String confirmRegistration(HttpServletRequest request, Model model, @RequestParam("token") String token) {
        Locale locale = request.getLocale();

        VerificationToken verificationToken = userAccountMgmtService.getVerificationToken(token);
        if (verificationToken == null) {
            String message = messages.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message", message);
            return "redirect:/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/badUser.html?lang=" + locale.getLanguage();
        }

        CHUserAccount CHUserAccount = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            model.addAttribute("message", messages.getMessage("auth.message.expired", null, locale));
            model.addAttribute("expired", true);
            model.addAttribute("token", token);
            return "redirect:/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/badUser.html?lang=" + locale.getLanguage();
        }

        CHUserAccount.setEnabled(true);

        userAccountMgmtService.saveRegisteredUser(CHUserAccount);

        model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale));

        return "redirect:/" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/login-form.html?lang=" + locale.getLanguage();
    }

    /*
    @GetMapping("/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/registrationConfirm")
    public ModelAndView confirmRegistration(final HttpServletRequest request, final ModelMap model, @RequestParam("token") final String token) throws UnsupportedEncodingException {
        Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());
        final String result = userAccountMgmtService.validateVerificationToken(token);
        if (result.equals("valid")) {
            final User user = userAccountMgmtService.getUser(token);
            authenticateWithoutPassword(user);
            model.addAttribute("messageKey", "message.accountVerified");
            return new ModelAndView("redirect:/" + AppConstants.DEFAULT_PAGES_DASHBOARD_PREFIX_PATH + "/protected/admin/console", model);
        }

        model.addAttribute("messageKey", "auth.message." + result);
        model.addAttribute("expired", "expired".equals(result));
        model.addAttribute("token", token);
        return new ModelAndView("redirect:/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/badUser", model);
    }
    */

    @GetMapping("/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/badUser")
    public ModelAndView badUser(final HttpServletRequest request, final ModelMap model, @RequestParam("messageKey" ) final Optional<String> messageKey, @RequestParam("expired" ) final Optional<String> expired, @RequestParam("token" ) final Optional<String> token) {

        Locale locale = request.getLocale();
        messageKey.ifPresent( key -> {
                    String message = messages.getMessage(key, null, locale);
                    model.addAttribute("message", message);
                }
        );

        expired.ifPresent( e -> model.addAttribute("expired", e));
        token.ifPresent( t -> model.addAttribute("token", t));

        return new ModelAndView(AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/badUser", model);
    }

    public void authenticateWithoutPassword(CHUserAccount CHUserAccount) {

        List<Privilege> privileges = CHUserAccount.getRoles()
                .stream()
                .map(Role::getPrivileges)
                .flatMap(Collection::stream)
                .distinct()
                .toList();

        List<GrantedAuthority> authorities = privileges.stream()
                .map(p -> new SimpleGrantedAuthority(p.getName()))
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(CHUserAccount, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
