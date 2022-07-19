package com.bluesky.mainservice.config.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class UnauthenticatedRequestHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        //미인증 사용자가 관리자 페이지로 접근하려는 경우 404 에러로 관리자 페이지의 존재를 숨김
        if (request.getRequestURI().startsWith("/admin")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //사용자가 원래 요청했던 주소를 가져온다
        String originUrl = request.getRequestURL().toString();
        String loginPageUrl = "/login";

        //로그인 전에 머물렀던 주소를 쿼리 스트링으로 추가한다
        String redirectUrl = UriComponentsBuilder.fromUriString(loginPageUrl)
                .queryParam("originUrl", originUrl)
                .build().toUriString();

        response.setHeader("Location", redirectUrl);
        response.setStatus(HttpServletResponse.SC_FOUND);
    }
}
