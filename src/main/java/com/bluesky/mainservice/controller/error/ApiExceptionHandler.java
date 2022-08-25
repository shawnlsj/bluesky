package com.bluesky.mainservice.controller.error;

import com.bluesky.mainservice.controller.community.board.BoardApiController;
import com.bluesky.mainservice.controller.error.dto.Error;
import com.bluesky.mainservice.controller.user.UserApiController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;

@RestControllerAdvice(assignableTypes = {UserApiController.class, BoardApiController.class})
public class ApiExceptionHandler {

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, ValidationException.class})
    public Error handleBeanValidationEx() {
        String message = "요청 값이 올바르지 않습니다.";
        return new Error(message);
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public Error handleError(RuntimeException e) {
        String message = "서버에 오류가 발생하였습니다.";
        return new Error(message);
    }
}
