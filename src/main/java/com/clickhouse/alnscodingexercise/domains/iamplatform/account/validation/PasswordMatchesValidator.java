package com.clickhouse.alnscodingexercise.domains.iamplatform.account.validation;

import com.clickhouse.alnscodingexercise.domains.iamplatform.account.web.requests.CreateUserAccountRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {
        //
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final CreateUserAccountRequestDTO user = (CreateUserAccountRequestDTO) obj;
        return user.getPassword().equals(user.getMatchingPassword());
    }

}
