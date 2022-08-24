package com.bluesky.mainservice.controller.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

public class UserResponseDto {
    @Getter
    public static class UserProfile {

        private final String profileImage;
        private final String nickname;
        private final int boardCount;
        private final int replyCount;
        private final boolean isMyself;

        @Builder
        public UserProfile(@NonNull String profileImage,
                           @NonNull String nickname,
                           @NonNull Integer boardCount,
                           @NonNull Integer replyCount,
                           @NonNull Boolean isMyself) {
            this.profileImage = profileImage;
            this.nickname = nickname;
            this.boardCount = boardCount;
            this.replyCount = replyCount;
            this.isMyself = isMyself;
        }
    }

    @Getter
    public static class MyInformation {

        private final String profileImage;
        private final String maskedEmail;
        private final String nickname;
        private final String accountType;
        private final LocalDateTime joinDate;
        private final int boardCount;
        private final int replyCount;

        @Builder
        private MyInformation(@NonNull String profileImage,
                              @NonNull String maskedEmail,
                              @NonNull String nickname,
                              @NonNull String accountType,
                              @NonNull LocalDateTime joinDate,
                              @NonNull Integer boardCount,
                              @NonNull Integer replyCount) {
            this.profileImage = profileImage;
            this.maskedEmail = maskedEmail;
            this.nickname = nickname;
            this.accountType = accountType;
            this.joinDate = joinDate;
            this.boardCount = boardCount;
            this.replyCount = replyCount;
        }
    }

    @Getter
    public static class JoinFormAttribute {
        
        private final String token;
        private final String maskedEmail;
        private final boolean isOriginalUser;
        private final String joinUrl;

        public JoinFormAttribute(String token, String maskedEmail, boolean isOriginalUser) {
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

    @RequiredArgsConstructor
    @Getter
    public static class SendEmailResult {
        private final boolean isSuccess;
    }

    @RequiredArgsConstructor
    @Getter
    public static class CheckNicknameResult {
        private final boolean isAvailable;
    }
}
