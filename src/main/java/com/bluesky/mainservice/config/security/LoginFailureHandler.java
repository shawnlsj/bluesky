package com.bluesky.mainservice.config.security;

import com.bluesky.mainservice.util.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.debug("로그인 실패", exception);
        if (exception instanceof OAuth2AuthenticationException) {
            Cookie cookie = CookieUtils.createMessageCookie("정상적인 접근이 아닙니다.");
            response.addCookie(cookie);
            response.sendRedirect("/");
        } else {
            request.getRequestDispatcher("/login").forward(request, response);
        }
    }
}
