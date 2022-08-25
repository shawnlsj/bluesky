package com.bluesky.mainservice.config.filter;

import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class XssFilter extends XssEscapeServletFilter {

    private static final List<String> targetPathList = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig){
        targetPathList.add("/board");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String url = httpServletRequest.getRequestURI();
        String method = httpServletRequest.getMethod();

        //필터링 대상 url 일 경우와 리소스 변경 메소드일 경우만 xss 필터링 진행
        if (startWithTargetPath(url) && anyMatch(method, "POST", "PUT", "PATCH")) {
            super.doFilter(request, response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean startWithTargetPath(String url) {
        return targetPathList.stream().anyMatch(url::startsWith);
    }

    private boolean anyMatch(String target, String... args) {
        return Arrays.asList(args).contains(target);
    }
}
