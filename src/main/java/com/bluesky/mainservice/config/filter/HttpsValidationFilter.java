package com.bluesky.mainservice.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HttpsValidationFilter implements Filter {
    private static final List<String> targetPathList = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) {
        targetPathList.add("/api");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String requestUrl = httpServletRequest.getRequestURL().toString();

        //대상 경로로 시작하면서 유효한 요청이 아닐 경우
        //요청 주소에서 http 를 https 로 바꿔 리다이렉트
        if (startWithTargetPath(requestUrl) && !isValid(httpServletRequest)) {
            String redirectLocation = "https" + httpServletRequest.getRequestURL().substring("http".length());
            httpServletResponse.sendRedirect(redirectLocation);
            return;
        }

        log.debug("passed request url: {}", httpServletRequest.getRequestURL());
        chain.doFilter(request, response);
    }

    private boolean startWithTargetPath(String url) {
        return targetPathList.stream().anyMatch(url::startsWith);
    }

    private boolean isValid(HttpServletRequest request) {
        String xForwardedProto = request.getHeader("X-Forwarded-Proto");
        return request.getScheme().equals("https") ||
                (StringUtils.hasText(xForwardedProto) && xForwardedProto.equals("https"));
    }
}
