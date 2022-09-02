package com.bluesky.mainservice.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CookieUtils {

    public static final String MESSAGE_COOKIE_NAME = "message";
    public static final String REDIRECT_COOKIE_NAME = "redirect";
    private static final String PREFIX_HOST = "__Host-";

    //--------리다이렉트 쿠키--------
    public static Cookie createRedirectCookie(String redirectUrl) {
        return CookieUtils.createHostSessionCookie(REDIRECT_COOKIE_NAME, redirectUrl, true);
    }

    public static void removeRedirectCookie(HttpServletResponse response) {
        removeHostCookie(response, REDIRECT_COOKIE_NAME, true);
    }

    public static String resolveRedirectCookie(HttpServletRequest request) {
        return findValue(request, PREFIX_HOST + REDIRECT_COOKIE_NAME);
    }

    //--------메세지 쿠키--------
    public static Cookie createMessageCookie(String message) {
        return createMessageCookie(message, "/");
    }

    public static Cookie createMessageCookie(String message, String path) {
        Cookie cookie = new Cookie(MESSAGE_COOKIE_NAME, URLEncoder.encode(message, StandardCharsets.UTF_8));
        cookie.setPath(path);
        return cookie;
    }

    public static void removeMessageCookie(HttpServletResponse response) {
        removeMessageCookie(response, "/");
    }

    public static void removeMessageCookie(HttpServletResponse response, String path) {
        removeCookie(response, MESSAGE_COOKIE_NAME, path);
    }

    public static String resolveMessageCookie(HttpServletRequest request) {
        return URLDecoder.decode(findValue(request, MESSAGE_COOKIE_NAME), StandardCharsets.UTF_8);
    }

    //-------일반 기능---------
    public static Cookie createHostSessionCookie(String key, String value, boolean addPrefix) {
        return createHostSessionCookie(key, value, addPrefix, "/");
    }

    public static Cookie createHostSessionCookie(String key, String value, boolean addPrefix, String path) {
        if (addPrefix) {
            key = PREFIX_HOST + key;
        }
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(path);
        return cookie;
    }

    public static void removeHostCookie(HttpServletResponse response, String key, boolean addPrefix) {
        removeHostCookie(response, key, addPrefix, "/");
    }

    public static void removeHostCookie(HttpServletResponse response, String key, boolean addPrefix, String path) {
        Cookie cookie = createHostSessionCookie(key, "", addPrefix, path);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public static void removeCookie(HttpServletResponse response, String key) {
        removeCookie(response, key, "/");
    }

    public static void removeCookie(HttpServletResponse response, String key, String path) {
        Cookie cookie = new Cookie(key, "");
        cookie.setPath(path);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public static Cookie findCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static String findValue(HttpServletRequest request, String key) {
        Cookie cookie = findCookie(request, key);
        if (cookie != null) {
            return cookie.getValue();
        }
        return "";
    }
}
