package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.validation.aspects;

import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.validation.AuthorizationUtils;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.validation.annotations.FgaCheck;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class FgaAspect {

    private final Logger logger = LoggerFactory.getLogger(FgaAspect.class);

    private final AuthorizationUtils authorizationUtils;

    @Before("@annotation(fga)")
    public void checkFGAPermission(final JoinPoint jointPoint, final FgaCheck fga) {
        logger.debug("**** CUSTOM AOP CALLED *****");

        boolean bolResult = authorizationUtils.checkFGAPermissionFromAspect(jointPoint);

        if (!bolResult) {
            throw new AccessDeniedException("Access Denied");
        }

    }

}
