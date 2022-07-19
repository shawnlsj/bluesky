package com.bluesky.mainservice.config.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class UnauthorizedRequestHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        //권한이 없는 사용자가 관리자 페이지로 접근하려는 경우 404 에러로 관리자 페이지의 존재를 숨김
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
