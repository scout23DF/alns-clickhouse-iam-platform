package com.clickhouse.alnscodingexercise.eventlisteners.events;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.models.entities.CHUserAccount;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationEvent;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class OnRegistrationCompletedEvent extends ApplicationEvent {

    private final CHUserAccount chUserAccount;

    public OnRegistrationCompletedEvent(CHUserAccount CHUserAccount) {
        super(CHUserAccount);
        this.chUserAccount = CHUserAccount;
    }

}
