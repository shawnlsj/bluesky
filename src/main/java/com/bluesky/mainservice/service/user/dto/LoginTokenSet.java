package com.bluesky.mainservice.service.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 로그인에 사용할 액세스 토큰과 리프레시 토큰을 포함합니다.
 */
@RequiredArgsConstructor
@Getter
public class LoginTokenSet {

    private final String accessToken;
    private final String refreshToken;
}
