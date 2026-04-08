package com.clickhouse.alnscodingexercise.wiring.config;

import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.config.InitializeOpenFgaData;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.config.OpenFgaConnectionDetails;
import com.clickhouse.alnscodingexercise.domains.iamplatform.authz.services.IAuthorizationService;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

// @AutoConfiguration
@Configuration
@EnableConfigurationProperties(OpenFgaProperties.class)
@EnableMethodSecurity
@RequiredArgsConstructor
public class OpenFgaAutoConfig {

    private final OpenFgaProperties openFgaProperties;

    @Bean
    @ConditionalOnMissingBean(OpenFgaConnectionDetails.class)
    public PropertiesOpenFgaConnectionDetails openFgaConnectionDetails() {
        return new PropertiesOpenFgaConnectionDetails(this.openFgaProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientConfiguration openFgaConfig(OpenFgaConnectionDetails connectionDetails) {

        // For simplicity, this creates a client with NO AUTHENTICATION. It is NOT SUITABLE FOR PRODUCTION USE!!
        return new ClientConfiguration()
                .apiUrl(connectionDetails.getFgaApiUrl())
                .storeId(openFgaProperties.getFgaStoreId())
                .authorizationModelId(openFgaProperties.getFgaAuthorizationModelId());
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenFgaClient openFgaClient(ClientConfiguration configuration) {
        try {
            return new OpenFgaClient(configuration);
        } catch (FgaInvalidParameterException e) {
            // TODO how to best handle
            throw new RuntimeException(e);
        }
    }

    @Bean
    @ConditionalOnBean({OpenFgaClient.class})
    public InitializeOpenFgaData initializeOpenFgaData(IAuthorizationService authorizationService,
                                                       OpenFgaProperties properties) {
        return new InitializeOpenFgaData(properties, authorizationService);
    }

    private static class PropertiesOpenFgaConnectionDetails implements OpenFgaConnectionDetails {

        private final OpenFgaProperties openFgaProperties;

        public PropertiesOpenFgaConnectionDetails(OpenFgaProperties openFgaProperties) {
            this.openFgaProperties = openFgaProperties;
        }

        @Override
        public String getFgaApiUrl() {
            return this.openFgaProperties.getFgaApiUrl();
        }
    }

    /*
    @Bean
    static PrePostTemplateDefaults prePostTemplateDefaults() {
        return new PrePostTemplateDefaults();
    }
    */
}
