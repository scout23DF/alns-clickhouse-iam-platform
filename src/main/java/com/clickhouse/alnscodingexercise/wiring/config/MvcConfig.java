package com.clickhouse.alnscodingexercise.wiring.config;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.validation.EmailValidator;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.validation.PasswordMatchesValidator;
import com.clickhouse.alnscodingexercise.domains.shared.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.server.servlet.ConfigurableServletWebServerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private MessageSource messageSource;

    public MvcConfig(MessageSource messageSource) {
        super();
        this.messageSource = messageSource;
    }

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:" + AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/doLogin");
        registry.addViewController("/home.html");
        registry.addViewController("/invalidSession.html");

        registry.addViewController(AppConstants.DEFAULT_PAGES_DASHBOARD_PREFIX_PATH + "/protected/dashboard-index.html");
        registry.addViewController(AppConstants.DEFAULT_PAGES_DASHBOARD_PREFIX_PATH + "/protected/admin/admin.html");
        registry.addViewController(AppConstants.DEFAULT_PAGES_DASHBOARD_PREFIX_PATH + "/protected/admin/console.html");

        registry.addViewController(AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/registration.html");
        registry.addViewController(AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/expiredAccount.html");
        registry.addViewController(AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/emailError.html");
        registry.addViewController(AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/successRegister.html");
        registry.addViewController(AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/forgetPassword.html");
        registry.addViewController(AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/updatePassword.html");
        registry.addViewController(AppConstants.DEFAULT_PAGES_IAM_ACCOUNT_PREFIX_PATH + "/changePassword.html");

        registry.addViewController(AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/login-form.html");
        registry.addViewController(AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/logout.html");
        registry.addViewController(AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/loginRememberMe");
        registry.addViewController(AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/doCustomLogin");
        registry.addViewController(AppConstants.DEFAULT_PAGES_IAM_AUTHN_PREFIX_PATH + "/logged-users.html");

        registry.addViewController(AppConstants.DEFAULT_PAGES_IAM_AUTHZ_PREFIX_PATH + "/roleHierarchy.html");
    }

    @Override
    public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/", "/resources/");
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        registry.addInterceptor(localeChangeInterceptor);
    }

    // beans

    @Bean
    public LocaleResolver localeResolver() {
        final CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        return cookieLocaleResolver;
    }

    @Bean
    public EmailValidator usernameValidator() {
        return new EmailValidator();
    }

    @Bean
    public PasswordMatchesValidator passwordMatchesValidator() {
        return new PasswordMatchesValidator();
    }

    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource);
        return validator;
    }

    @Bean
    WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> enableDefaultServlet() {
        return (factory) -> factory.setRegisterDefaultServlet(true);
    }

}