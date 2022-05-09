package com.bluesky.mainservice.config.argumentresolver;

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
        Device device = DeviceUtils.getCurrentDevice(webRequest);

        //요청 파라미터에서 mobile=true -> 모바일버전 화면, mobile=false -> pc버전 화면 출력해야함
        String mobileParam = webRequest.getParameter("mobile");
        if (mobileParam != null) {
            if (mobileParam.equals("true")) {
                return MobilePage.createAndSet(true);
            } else if (mobileParam.equals("false")) {
                return MobilePage.createAndSet(false);
            }
        }
        //파라미터로 따로 모바일 화면 요청을 안했으니 디바이스에 따라서 모바일 화면 출력 여부를 결정함
        return createMobilePage(device);
    }

    private MobilePage createMobilePage(Device device) {
        if (device.isMobile() || device.isTablet()) {
            return MobilePage.createAndSet(true);
        } else {
            return MobilePage.createAndSet(false);
        }
    }
}
