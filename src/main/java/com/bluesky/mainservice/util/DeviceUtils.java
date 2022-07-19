package com.bluesky.mainservice.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class DeviceUtils {

    public static boolean isMobile(HttpServletRequest request){
        String userAgent = request.getHeader("User-Agent");

        if (StringUtils.hasText(userAgent)) {
            userAgent = userAgent.toLowerCase();
            return userAgent.matches("android|webos|iphone|ipad|ipod|blackberry|iemobile|opera mini");
        }
        return false;
    }
}
