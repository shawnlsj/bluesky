package com.bluesky.mainservice.controller;

import com.bluesky.mainservice.controller.user.dto.LoginInfo;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class LoginInfoControllerAdvice {

    @ModelAttribute("loginInfo")
    public LoginInfo createLoginInfo() {
        boolean isLoginUser = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            isLoginUser = true;
        }
        return new LoginInfo(isLoginUser);
    }
}
