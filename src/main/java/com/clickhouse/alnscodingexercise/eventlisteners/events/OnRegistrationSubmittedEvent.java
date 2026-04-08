package com.clickhouse.alnscodingexercise.eventlisteners.events;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class OnRegistrationSubmittedEvent extends ApplicationEvent {

    private final CHUserAccount chUserAccount;
    private final Locale locale;
    private final String appUrl;

    public OnRegistrationSubmittedEvent(CHUserAccount CHUserAccount, Locale locale, String appUrl) {
        super(CHUserAccount);
        this.chUserAccount = CHUserAccount;
        this.locale = locale;
        this.appUrl = appUrl;
    }

}
