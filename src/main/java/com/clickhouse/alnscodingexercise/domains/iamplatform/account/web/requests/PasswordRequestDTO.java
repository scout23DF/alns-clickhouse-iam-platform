package com.clickhouse.alnscodingexercise.domains.iamplatform.account.web.requests;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.validation.ValidPassword;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordRequestDTO {

    private String oldPassword;
    private  String token;
    @ValidPassword
    private String newPassword;

}
