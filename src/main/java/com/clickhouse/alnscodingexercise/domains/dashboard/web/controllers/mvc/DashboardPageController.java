package com.clickhouse.alnscodingexercise.domains.dashboard.web.controllers.mvc;

import com.clickhouse.alnscodingexercise.domains.assetmgmt.models.dtos.ResourceThingDTO;
import com.clickhouse.alnscodingexercise.domains.assetmgmt.services.IResourceThingMgmtService;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.services.IUserAccountMgmtService;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.models.dtos.GenericObjectEnrichedWithACLDTO;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DashboardPageController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final IUserAccountMgmtService userAccountMgmtService;
    private final IResourceThingMgmtService resourceThingMgmtService;
    private final MessageSource messages;

    @GetMapping(AppConstants.DEFAULT_PAGES_DASHBOARD_PREFIX_PATH + "/handlePortalHome")
    public ModelAndView handlePortalHome(final HttpServletRequest request,
                                         final ModelMap model,
                                         @RequestParam("messageKey") final Optional<String> messageKey) {

        Locale locale = request.getLocale();
        messageKey.ifPresent( key -> {
                    String message = messages.getMessage(key, null, locale);
                    model.addAttribute("message", message);
                }
        );

        CHUserAccount authenticatedUser;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CHUserAccount) {
            authenticatedUser = (CHUserAccount) authentication.getPrincipal();
        } else {
            String username = authentication.getName();
            authenticatedUser = userAccountMgmtService.getUserByUsername(username);
        }

        List<GenericObjectEnrichedWithACLDTO<ResourceThingDTO>> resourcesThingsWithAclList = resourceThingMgmtService.listAllWithAclForUser(
                authenticatedUser
        );

        model.addAttribute("authenticatedUser", authenticatedUser);
        model.addAttribute("resourcesThingsList", resourcesThingsWithAclList);

        return new ModelAndView(
                AppConstants.DEFAULT_PAGES_DASHBOARD_PREFIX_PATH + "/protected/dashboard-index",
                model
        );
    }

    @GetMapping(AppConstants.DEFAULT_PAGES_DASHBOARD_PREFIX_PATH + "/console")
    public ModelAndView console(final HttpServletRequest request, final ModelMap model, @RequestParam("messageKey") final Optional<String> messageKey) {

        Locale locale = request.getLocale();
        messageKey.ifPresent( key -> {
                    String message = messages.getMessage(key, null, locale);
                    model.addAttribute("message", message);
                }
        );

        return new ModelAndView(AppConstants.DEFAULT_PAGES_DASHBOARD_PREFIX_PATH + "/protected/admin/console", model);
    }

}
