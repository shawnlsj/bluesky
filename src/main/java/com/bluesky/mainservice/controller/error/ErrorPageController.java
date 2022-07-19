package com.bluesky.mainservice.controller.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;

@Controller
@RequestMapping("/error")
public class ErrorPageController implements ErrorController {

    @RequestMapping
    String errorPage(HttpServletRequest request, Model model, HttpServletResponse response) {
        int statusCode = 0;

        //클라이언트가 직접 이 경로로 요청한 경우 404 에러로 응답한다
        if (request.getAttribute(ERROR_STATUS_CODE) == null) {
            model.addAttribute("statusCode", 404);
        } else {
            statusCode = (Integer) request.getAttribute(ERROR_STATUS_CODE);

            //5xx 에러 -> 500 에러로 응답한다
            if (statusCode / 500 == 1) {
                statusCode = 500;
            } else if (statusCode == 405 || statusCode == 404) {
                statusCode = 404;
            } else {
                statusCode = 400;
            }
            model.addAttribute("statusCode", statusCode);
        }
        response.setStatus(statusCode);
        return "error";
    }
}