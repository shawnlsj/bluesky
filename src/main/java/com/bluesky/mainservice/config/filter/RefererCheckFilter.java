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
public class RefererCheckFilter implements Filter {

    private static final List<String> whiteList = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) {
        whiteList.add("https://accounts.google.com/");
        whiteList.add("https://accounts.kakao.com/");
        whiteList.add("https://nid.naver.com/");
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String referer = httpServletRequest.getHeader("Referer");

        //GET 메소드가 아니면서 referer 헤더의 값이 있을 때만 필터링 진행
        if (!httpServletRequest.getMethod().equals("GET") && StringUtils.hasText(referer)) {

            //referer 에서 포트번호를 제외한 도메인 구하기
            String refererDomain = referer.split("/")[2].split(":")[0];

            //요청 url 에서 도메인 구하기
            String requestUrlDomain = request.getServerName();

            //다음 2가지 조건을 모두 만족하면 bad request 응답
            //1. referer 와 요청 url 의 도메인이 일치하지 않는다.
            //2. 화이트리스트에 없다.
            if (!refererDomain.equals(requestUrlDomain) && !isIncludedInWhiteList(referer)) {
                log.info("invalid referer: {}", referer);
                log.debug("referer: {}, requestUrl: {}", referer, httpServletRequest.getRequestURL());
                httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        log.debug("passed request url: {}", httpServletRequest.getRequestURL());
        chain.doFilter(request, response);
    }

    private boolean isIncludedInWhiteList(String referer){
        return whiteList.stream().anyMatch(referer::contains);
    }
}