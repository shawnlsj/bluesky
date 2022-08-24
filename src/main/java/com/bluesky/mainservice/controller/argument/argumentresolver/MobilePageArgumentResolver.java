package com.bluesky.mainservice.controller.argument.argumentresolver;

import com.bluesky.mainservice.controller.argument.MobilePage;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.bluesky.mainservice.config.interceptor.ResolveViewModeInterceptor.MOBILE_MODE;
import static com.bluesky.mainservice.config.interceptor.ResolveViewModeInterceptor.VIEW_MODE_KEY;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

public class MobilePageArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return MobilePage.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String viewMode = (String) webRequest.getAttribute(VIEW_MODE_KEY, SCOPE_REQUEST);
        if (StringUtils.hasText(viewMode) && viewMode.equals(MOBILE_MODE)) {
            return new MobilePage(true);
        }
        return new MobilePage(false);
    }
}
