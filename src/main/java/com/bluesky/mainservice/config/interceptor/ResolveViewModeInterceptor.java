package com.bluesky.mainservice.config.interceptor;

import com.bluesky.mainservice.util.CookieUtils;
import com.bluesky.mainservice.util.DeviceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResolveViewModeInterceptor implements HandlerInterceptor {

    public static final String VIEW_MODE_KEY = "view";
    public static final String DESKTOP_MODE = "desktop";
    public static final String MOBILE_MODE = "mobile";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        //뷰모드가 파라미터로 넘어왔다면 뷰모드를 설정하면서 그 값을 쿠키에 넣는다
        //파라미터가 없는 경우는 쿠키를 확인한다
        String viewMode = request.getParameter(VIEW_MODE_KEY);
        if (StringUtils.hasText(viewMode)) {
            switch (viewMode) {
                case DESKTOP_MODE:
                    request.setAttribute(VIEW_MODE_KEY, DESKTOP_MODE);
                    response.addCookie(new Cookie(VIEW_MODE_KEY, DESKTOP_MODE));
                    return true;

                case MOBILE_MODE:
                    request.setAttribute(VIEW_MODE_KEY, MOBILE_MODE);
                    response.addCookie(new Cookie(VIEW_MODE_KEY, MOBILE_MODE));
                    return true;
            }
        }
        viewMode = CookieUtils.findValue(request, VIEW_MODE_KEY);
        if (StringUtils.hasText(viewMode) &&
                (viewMode.equals(DESKTOP_MODE) || viewMode.equals(MOBILE_MODE))) {
            request.setAttribute(VIEW_MODE_KEY, viewMode);
            return true;
        }

        //쿠키도 없을 경우에는 디바이스를 기준으로 뷰모드를 설정한다
        if (DeviceUtils.isMobile(request)) {
            request.setAttribute(VIEW_MODE_KEY, MOBILE_MODE);
        } else {
            request.setAttribute(VIEW_MODE_KEY, DESKTOP_MODE);
        }
        return true;


    }
}
