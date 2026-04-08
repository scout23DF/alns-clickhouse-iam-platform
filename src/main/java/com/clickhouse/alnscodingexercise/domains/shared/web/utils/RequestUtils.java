package com.clickhouse.alnscodingexercise.domains.shared.web.utils;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtils {

    public static String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

}
