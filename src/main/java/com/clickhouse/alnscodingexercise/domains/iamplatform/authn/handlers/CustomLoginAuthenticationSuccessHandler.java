package com.clickhouse.alnscodingexercise.domains.iamplatform.authn.handlers;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authn.models.dtos.ActiveUserStoreDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authn.models.dtos.LoggedUserDTO;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
@Setter
@Getter
public class CustomLoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    // @Autowired
    // private ActiveUserStoreDTO activeUserStoreDTO;

    @Autowired
    private Environment env;

    @Override
    public void onAuthenticationSuccess(final @NonNull HttpServletRequest request,
                                        final @NonNull HttpServletResponse response,
                                        final @NonNull Authentication authentication)
            throws IOException {

        handle(request, response, authentication);
        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.setMaxInactiveInterval(30 * 60);

            String username;
            if (authentication.getPrincipal() instanceof CHUserAccount) {
                username = ((CHUserAccount) authentication.getPrincipal()).getUsername();
            } else {
                username = authentication.getName();
            }
            // LoggedUserDTO loggedUser = new LoggedUserDTO(username, activeUserStoreDTO);
            LoggedUserDTO loggedUser = new LoggedUserDTO(username, new ActiveUserStoreDTO());
            session.setAttribute("user", loggedUser);
        }
        clearAuthenticationAttributes(request);
    }

    protected void handle(final HttpServletRequest request,
                          final HttpServletResponse response,
                          final Authentication authentication)
            throws IOException {

        final String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(final Authentication authentication) {
        boolean isUser = false;
        boolean isAdmin = false;
        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (final GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals("READ_PRIVILEGE")) {
                isUser = true;
            } else if (grantedAuthority.getAuthority().equals("WRITE_PRIVILEGE")) {
                isAdmin = true;
                isUser = false;
                break;
            }
        }
        if (isUser) {
            String username;
            if (authentication.getPrincipal() instanceof CHUserAccount) {
                username = ((CHUserAccount) authentication.getPrincipal()).getEmail();
            } else {
                username = authentication.getName();
            }

            // return "/" + AppConstants.DEFAULT_PAGES_DASHBOARD_PREFIX_PATH + "/protected/dashboard-index.html?user=" + username;
            return "/" + AppConstants.DEFAULT_PAGES_DASHBOARD_PREFIX_PATH + "/handlePortalHome";
        } else if (isAdmin) {
            return "/" + AppConstants.DEFAULT_PAGES_DASHBOARD_PREFIX_PATH + "/console";
        } else {
            throw new IllegalStateException();
        }
    }

    protected void clearAuthenticationAttributes(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

}