package com.bluesky.mainservice.config.security.jwt;

import com.bluesky.mainservice.util.CookieUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtMapper {

    public static final String ACCESS_TOKEN_COOKIE_NAME = "__Host-access_token";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "__Host-refresh_token";


    public static void addAccessToken(HttpServletResponse response, String accessToken) {
        response.addCookie(createAccessTokenCookie(accessToken));
    }

    public static void addRefreshToken(HttpServletResponse response, String refreshToken) {
        response.addCookie(createRefreshTokenCookie(refreshToken));
    }

    public static void removeAccessToken(HttpServletResponse response) {
        Cookie cookie = CookieUtils.createHostSessionCookie(ACCESS_TOKEN_COOKIE_NAME, "", false);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public static void removeRefreshToken(HttpServletResponse response) {
        Cookie cookie = CookieUtils.createHostSessionCookie(REFRESH_TOKEN_COOKIE_NAME, "", false);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public static String resolveAccessToken(HttpServletRequest request) {
        return CookieUtils.findValue(request, ACCESS_TOKEN_COOKIE_NAME);
    }

    public static String resolveRefreshToken(HttpServletRequest request) {
        return CookieUtils.findValue(request, REFRESH_TOKEN_COOKIE_NAME);
    }

    private static Cookie createAccessTokenCookie(String accessToken) {
        Cookie cookie = CookieUtils.createHostSessionCookie(ACCESS_TOKEN_COOKIE_NAME, accessToken, false);
        int maxAge = (int) (JwtGenerator.ACCESS_TOKEN_EXPIRATION_TIME / 1000L);
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    private static Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = CookieUtils.createHostSessionCookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken, false);
        int maxAge = (int) (JwtGenerator.REFRESH_TOKEN_EXPIRATION_TIME / 1000L);
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}
