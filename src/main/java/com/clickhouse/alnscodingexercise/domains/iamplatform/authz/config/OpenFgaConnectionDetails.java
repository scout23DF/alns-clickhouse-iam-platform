package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.config;

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

public interface OpenFgaConnectionDetails extends ConnectionDetails {

    String getFgaApiUrl();

}
