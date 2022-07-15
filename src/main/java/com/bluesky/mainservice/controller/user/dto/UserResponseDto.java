package com.bluesky.mainservice.controller.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserResponseDto {

    @Getter
    public static class JoinUserInfo {
        String token;
        String maskedEmail;
        boolean isOriginalUser;
        String joinUrl;

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
        boolean isSuccess;
    }

    @AllArgsConstructor
    @Getter
    public static class CheckNicknameResult {
        boolean isAvailable;
    }
}
