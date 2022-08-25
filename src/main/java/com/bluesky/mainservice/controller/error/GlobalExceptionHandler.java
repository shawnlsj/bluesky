package com.bluesky.mainservice.controller.error;

import com.bluesky.mainservice.util.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConversionFailedException.class)
    public void handleConflict(ConversionFailedException e, HttpServletResponse response) throws IOException {
        log.debug("path variable 변환 실패",e);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @ExceptionHandler(value = DataAccessException.class)
    public String handleDataAccessEx(DataAccessException e, HttpServletResponse response) {
        log.info("데이터베이스 에러 발생", e);
        String message = "서버에 오류가 발생하였습니다.";
        response.addCookie(CookieUtils.createMessageCookie(message));
        return "redirect:/";
    }
}
