package com.clickhouse.alnscodingexercise.domains.dashboard.web.controllers.mvc;

import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DashboardPageController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final MessageSource messages;

    @GetMapping(AppConstants.DEFAULT_PAGES_DASHBOARD_PREFIX_PATH + "/console")
    public ModelAndView console(final HttpServletRequest request, final ModelMap model, @RequestParam("messageKey") final Optional<String> messageKey) {

        Locale locale = request.getLocale();
        messageKey.ifPresent( key -> {
                    String message = messages.getMessage(key, null, locale);
                    model.addAttribute("message", message);
                }
        );

        return new ModelAndView(AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/protected/admin/console", model);
    }

}
