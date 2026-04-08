package com.clickhouse.alnscodingexercise.domains.iamplatform.authn.web.controllers.mvc;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.services.IUserAccountMgmtService;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authn.models.dtos.ActiveUserStoreDTO;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthenticationPageController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final MessageSource messages;
    private final IUserAccountMgmtService userAccountMgmtService;
    private final ActiveUserStoreDTO activeUserStoreDTO;

    @GetMapping("/" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/doLogin")
    public ModelAndView login(final HttpServletRequest request,
                              final ModelMap model,
                              @RequestParam("messageKey") final Optional<String> messageKey,
                              @RequestParam("error") final Optional<String> error) {

        Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());
        messageKey.ifPresent( key -> {
                    String message = messages.getMessage(key, null, locale);
                    model.addAttribute("message", message);
                }
        );

        error.ifPresent( e ->  model.addAttribute("error", e));

        return new ModelAndView(AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/login-form", model);
    }

    @GetMapping("/" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/loggedUsers")
    public String getLoggedUsers(final Locale locale, final Model model) {
        model.addAttribute("users", activeUserStoreDTO.getUsers());
        return AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/logged-users";
    }

    @GetMapping("/" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/loggedUsersFromSessionRegistry")
    public String getLoggedUsersFromSessionRegistry(final Locale locale, final Model model) {
        model.addAttribute("users", userAccountMgmtService.getUsersFromSessionRegistry());
        return AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/logged-users";
    }

}
