package com.clickhouse.alnscodingexercise.domains.iamplatform.authz.validation.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreOpenFgaCheck(userType="'user'", relation="'reader'", objectType="'document'", object="")
public @interface PreReadDocumentCheck {
	@AliasFor(attribute = "object", annotation = PreOpenFgaCheck.class)
	String value();
}
