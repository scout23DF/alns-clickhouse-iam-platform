package com.clickhouse.alnscodingexercise.domains.iamplatform.authn.services;

public interface ILoginAttemptService {

    void loginFailed(final String key);
    boolean isBlocked();

}
