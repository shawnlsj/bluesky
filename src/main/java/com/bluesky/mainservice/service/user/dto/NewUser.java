package com.bluesky.mainservice.service.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * 사용자를 회원으로 등록하기 위한 정보를 포함합니다.
 */
@Getter
public class NewUser {

    private final String email;
    private final String nickname;
    private final String password;
    private final boolean isOriginalUser;

    @Builder
    private NewUser(String email,
                    String password,
                    @NonNull String nickname,
                    @NonNull Boolean isOriginalUser) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.isOriginalUser = isOriginalUser;
    }
}
