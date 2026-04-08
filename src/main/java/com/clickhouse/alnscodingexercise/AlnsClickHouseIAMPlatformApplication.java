package com.clickhouse.alnscodingexercise;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.request.RequestContextListener;

@Slf4j
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableWebSecurity
@EnableTransactionManagement
public class AlnsClickHouseIAMPlatformApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		log.info("Starting AlnsClickHouseIAMPlatformApplication...");
		SpringApplication.run(AlnsClickHouseIAMPlatformApplication.class, args);
		log.info("AlnsClickHouseIAMPlatformApplication started.");
	}

	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}

}
