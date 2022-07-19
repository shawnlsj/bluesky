package com.bluesky.mainservice.service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인에 사용할 액세스 토큰과 리프레시 토큰을 포함합니다.
 */
@AllArgsConstructor
@Getter
public class LoginTokenSet {
    String accessToken;
    String refreshToken;
}
