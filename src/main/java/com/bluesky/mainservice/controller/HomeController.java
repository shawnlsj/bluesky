package com.bluesky.mainservice.controller;

import com.bluesky.mainservice.controller.argument.LoginUser;
import com.bluesky.mainservice.util.CookieUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestMapping("/")
@Controller
public class HomeController {

    @GetMapping
    public String home(HttpServletRequest request, HttpServletResponse response, LoginUser loginUser, Model model) {

        if (loginUser != null) {
            model.addAttribute("isLoginUser", true);
        }

        //쿠키에서 화면에 출력할 메시지를 획득
        String message = CookieUtils.resolveMessageCookie(request);
        if (StringUtils.hasText(message)) {
            //메시지를 획득한 쿠키를 삭제
            CookieUtils.removeMessageCookie(response);

            //화면으로 메시지를 전달
            model.addAttribute("message", message);
        }
        return "index";
    }
}
