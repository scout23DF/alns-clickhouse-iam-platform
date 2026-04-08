package com.clickhouse.alnscodingexercise.domains.shared.validation;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class ValidatorUtils {

    public static boolean isValidEmail(String valueToCheck) {
        return StringUtils.isNotEmpty(valueToCheck) && StringUtils.contains(valueToCheck, "@");
    }

}
