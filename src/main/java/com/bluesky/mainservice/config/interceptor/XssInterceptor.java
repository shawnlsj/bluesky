package com.bluesky.mainservice.config.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class XssInterceptor implements HandlerInterceptor  {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getParameter("content") != null) {
            if (request.getParameter("content").toString().contains("<!-- Not Allowed Tag Filtered")
                    || request.getParameter("content").toString().contains("<!-- Not Allowed Attribute Filtered")) {
                try {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                } catch (IOException e) {
                    log.error("url : {} ", request.getRequestURI(), e);
                }
                return false;
            }
        }
        return true;
    }
}
