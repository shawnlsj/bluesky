package com.bluesky.mainservice.service.user.exception;

/**
 * 사용자 조회 결과가 존재해야 하지만
 * 조회된 결과가 없을 때 발생합니다.
 */
public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException() {
        super("존재하지 않는 사용자 입니다.");
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
