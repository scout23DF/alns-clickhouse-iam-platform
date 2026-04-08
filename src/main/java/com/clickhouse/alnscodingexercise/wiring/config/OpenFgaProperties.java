package com.clickhouse.alnscodingexercise.wiring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix="openfga")
@Getter
@Setter
public class OpenFgaProperties {

    private String fgaApiUrl;
    private String fgaStoreId;
    private String fgaStoreName;
    private String fgaAuthorizationModelId;
    private String fgaApiTokenIssuer;
    private String fgaApiAudience;
    private String fgaClientId;
    private String fgaClientSecret;
    public boolean fgaShouldImportInitialStructure;
    private String fgaInitialModelSchemaToImport;
    private List<String> fgaInitialRelationshipTuplesToImport;

    // private static final String FGA_MODEL_SCHEMA_DEFAULT = "classpath:openfga/initial-model-tuples-data/domain-document-mgmt-schema.json";
    // private static final List<String> FGA_RELATIONSHIP_TUPLE_DEFAULT = List.of("classpath:openfga/initial-model-tuples-data/domain-document-mgmt-tuple.json");

}
