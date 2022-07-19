package com.bluesky.mainservice.service.user.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 사용자를 회원으로 등록하기 위한 정보를 포함합니다.
 */
@Builder
@Getter
public class UserRegisterData {
    String email;
    String nickname;
    String password;
    boolean isOriginalUser;
}
