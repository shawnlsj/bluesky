package com.bluesky.mainservice.controller.user.dto;

import com.bluesky.mainservice.repository.user.constant.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class UserResponseDto {

    @Builder
    @Getter
    public static class UserAccount {
        private final String maskedEmail;
        private final String nickname;
        private final String accountType;
        private final LocalDateTime joinDate;
    }

    @Getter
    public static class JoinUserInfo {
        private final String token;
        private final String maskedEmail;
        private final boolean isOriginalUser;
        private final String joinUrl;

        public JoinUserInfo(String token, String maskedEmail, boolean isOriginalUser) {
            this.token = token;
            this.maskedEmail = maskedEmail;
            this.isOriginalUser = isOriginalUser;
            if (isOriginalUser) {
                joinUrl = "/join";
            } else {
                joinUrl = "/join/oauth2";
            }
        }
    }

    @AllArgsConstructor
    @Getter
    public static class SendEmailResult {
        private final boolean isSuccess;
    }

    @AllArgsConstructor
    @Getter
    public static class CheckNicknameResult {
        private final boolean isAvailable;
    }
}
