package com.bluesky.mainservice.controller.error;

import com.bluesky.mainservice.controller.error.dto.ErrorInfo;
import com.bluesky.mainservice.controller.user.UserApiController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {UserApiController.class})
public class ApiExceptionHandler {

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ErrorInfo handleBeanValidationEx() {
        String message = "요청 값이 올바르지 않습니다.";
        return new ErrorInfo(message);
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorInfo handleError(Exception e) {
        String message = "서버에 문제가 발생하였습니다.";
        return new ErrorInfo(message);
    }
}
