package com.bluesky.mainservice.argumentresolver;

import com.bluesky.mainservice.controller.argument.MobilePage;
import org.springframework.core.MethodParameter;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class MobilePageArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return MobilePage.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        MobilePage page;
        Device device = DeviceUtils.getCurrentDevice(webRequest);

        //쿼리 스트링에서 mobile=true 로 되어있는지 확인한다
        boolean isMobile = false;
        String mobileParameter = webRequest.getParameter("mobile");
        if (mobileParameter != null) {
            if (mobileParameter.equals("true")) {
                isMobile = true;
            }
        }

        if (device.isMobile() || device.isTablet() || isMobile) {
            page = MobilePage.createAndSet(true);
        } else {
            page = MobilePage.createAndSet(false);
        }
        return page;
    }
}
