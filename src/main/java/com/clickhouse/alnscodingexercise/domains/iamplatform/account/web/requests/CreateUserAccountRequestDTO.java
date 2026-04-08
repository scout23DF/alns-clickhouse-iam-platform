package com.clickhouse.alnscodingexercise.domains.iamplatform.account.web.requests;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.validation.PasswordMatches;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.validation.ValidEmail;
import com.clickhouse.alnscodingexercise.domains.iamplatform.account.validation.ValidPassword;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@PasswordMatches
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserAccountRequestDTO {
    @NotNull
    @Size(min = 1, message = "{Size.userDto.firstName}")
    private String firstName;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.lastName}")
    private String lastName;

    @NotNull
    @Size(min = 5, max = 40, message = "{Size.userDto.username}")
    private String username;

    @ValidPassword
    private String password;

    @NotNull
    @Size(min = 1)
    private String matchingPassword;

    @ValidEmail
    @NotNull
    @Size(min = 1, message = "{Size.userDto.email}")
    private String email;

    private Integer role;

}
