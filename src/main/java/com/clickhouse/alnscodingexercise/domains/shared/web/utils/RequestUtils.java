package com.clickhouse.alnscodingexercise.domains.shared.web.utils;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.services.IUserAccountMgmtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestUtils {

    private final IUserAccountMgmtService userAccountMgmtService;

    public static String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    public CHUserAccount getCurrentAuthenticatedUser() {
        CHUserAccount authenticatedUser;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        if (authentication.getPrincipal() instanceof CHUserAccount) {
            authenticatedUser = (CHUserAccount) authentication.getPrincipal();
        } else {
            String username = authentication.getName();
            authenticatedUser = userAccountMgmtService.getUserByUsername(username);
        }
        return authenticatedUser;
    }
}
