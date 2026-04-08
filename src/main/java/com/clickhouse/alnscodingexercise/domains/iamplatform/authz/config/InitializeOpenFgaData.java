package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.config;

import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.IAuthorizationService;
import com.clickhouse.alnscodingexercise.wiring.config.OpenFgaProperties;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * Creates an FGA store, writes a simple authorization model, and adds a single tuple of form:
 * <p>
 * This is for sample purposes only; would not be necessary in a real application.
 */
@Component
@RequiredArgsConstructor
public class InitializeOpenFgaData implements ResourceLoaderAware, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(InitializeOpenFgaData.class);

    private final OpenFgaProperties openFgaProperties;
    private final IAuthorizationService authorizationService;
    private volatile ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        authorizationService.loadInitialFgaStructure(this.openFgaProperties, resourceLoader);

    }

}
