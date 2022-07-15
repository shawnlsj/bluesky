package com.bluesky.mainservice.service.user.exception;

/**
 * 등록되지 않은 사용자에 대하여 처리를 해야 하는데
 * 이미 등록된 상태일 경우 발생합니다.
 */
public class UserAlreadyRegisteredException extends RuntimeException {

    public UserAlreadyRegisteredException(String message) {
        super(message);
    }

    public UserAlreadyRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }
}
