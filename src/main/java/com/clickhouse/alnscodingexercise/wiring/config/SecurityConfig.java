package com.clickhouse.alnscodingexercise.wiring.config;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.repositories.UserRepository;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authn.models.dtos.ActiveUserStoreDTO;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authn.handlers.CustomWebSecurityExpressionHandler;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

// @ImportResource({ "classpath:webSecurityConfig.xml" })
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final UserDetailsPasswordService userDetailsPasswordService;
    private final AuthenticationSuccessHandler myAuthenticationSuccessHandler;
    private final LogoutSuccessHandler myLogoutSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final UserRepository userRepository;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers("/resources/**", "/h2/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .securityContext((securityContext) -> securityContext.requireExplicitSave(true))
            .authorizeHttpRequests(authz -> {
                authz
                    .requestMatchers(HttpMethod.GET, "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/roleHierarchy")
                        .hasRole("STAFF")
                    .requestMatchers(
                            "/swagger-ui*",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**",
                            "/api/**",
                            "/login*",
                            "/pages/iam/authn/doLogin*",
                            "/logout*",
                            "/signin/**",
                            "/signup/**",
                            "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/customLogin",
                            "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/registration*",
                            "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/registrationConfirm*",
                            "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/expiredAccount*",
                            "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/badUser*",
                            "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/resendRegistrationToken*",
                            "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/validateResetPasswordToken*",
                            "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/forgetPassword*",
                            "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/resetPassword*",
                            "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/savePassword*",
                            "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/updatePassword*",
                            "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/changePassword*",
                            "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/emailError*",
                            "/resources/**",
                            "/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/successRegister*")
                    .permitAll()
                    .requestMatchers("/invalidSession*")
                    .anonymous()
                    .requestMatchers("/" + AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/updatePassword*")
                    .hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
                    .requestMatchers("/" + AppConstants.DEFAULT_PAGES_DASHBOARD_PREFIX_PATH + "/console")
                    .hasAuthority("READ_PRIVILEGE")
                    .anyRequest()
                    .hasAuthority("READ_PRIVILEGE");
            })
            .formLogin((formLogin) -> formLogin // .loginPage(AppConstants.DEFAULT_APP_AUTHN_PREFIX_PATH + "/do-login")
                .loginPage("/" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/doLogin")
                .defaultSuccessUrl("/homepage.html")
                .failureUrl("/" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/doLogin?error=true")
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                // .authenticationDetailsSource(authenticationDetailsSource)
                .permitAll()
            )
            .sessionManagement((sessionManagement) -> sessionManagement.invalidSessionUrl("/invalidSession.html")
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry()))
            .logout((logout) -> logout.logoutSuccessHandler(myLogoutSuccessHandler)
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/logout.html?logSucc=true")
                .deleteCookies("JSESSIONID")
                .permitAll())
            ; // .rememberMe((remember) -> remember.rememberMeServices(rememberMeServices()));

        return http.build();
    }

    // beans
    @Bean
    public SecurityExpressionHandler<FilterInvocation> customWebSecurityExpressionHandler() {
        CustomWebSecurityExpressionHandler expressionHandler = new CustomWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());

        return expressionHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        String hierarchy = "ROLE_ADMIN > ROLE_STAFF \n ROLE_STAFF > ROLE_USER";
        return RoleHierarchyImpl.fromHierarchy(hierarchy);
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public ActiveUserStoreDTO activeUserStore() {
        return new ActiveUserStoreDTO();
    }

    /*
    @Bean
    public DaoAuthenticationProvider authProvider() {
        final CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider(this.userDetailsService);
        authProvider.setUserDetailsPasswordService(userDetailsPasswordService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setPostAuthenticationChecks(differentLocationChecker());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(authProvider())
            .build();
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        CustomRememberMeServices rememberMeServices = new CustomRememberMeServices("theKey", userDetailsService, new InMemoryTokenRepositoryImpl());
        return rememberMeServices;
    }

    */

}
