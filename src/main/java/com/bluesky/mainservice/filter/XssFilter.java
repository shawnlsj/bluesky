package com.bluesky.mainservice.filter;

import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XssFilter extends XssEscapeServletFilter {

    private static final List excludeUrlList = new ArrayList();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        excludeUrlList.add("/css");
        excludeUrlList.add("/js");
        super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String url = ((HttpServletRequest) request).getRequestURI();
        if (isExcludeUrl(url)) {
            chain.doFilter(request, response);
        } else {
            super.doFilter(request, response, chain);
        }
    }

    private boolean isExcludeUrl(String url) {
        return excludeUrlList.stream().anyMatch(excludeUrl -> url.startsWith((String) excludeUrl));
    }
}
