package com.bluesky.mainservice.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class XssInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getParameter("content") != null) {
            if (request.getParameter("content").toString().contains("<!-- Not Allowed Tag Filtered")
                    || request.getParameter("content").toString().contains("<!-- Not Allowed Attribute Filtered")) {
                return false;
            }
        }
        return true;
    }
}
