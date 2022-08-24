package com.bluesky.mainservice.service.user.dto;

import com.bluesky.mainservice.repository.user.constant.AccountType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;

@Getter
public class UserProfile {

    private final String email;
    private final String nickname;
    private final AccountType accountType;
    private final String profileImage;
    private final LocalDateTime registerDate;
    private final int boardCount;
    private final int replyCount;

    @Builder
    private UserProfile(@NonNull String email,
                        @NonNull String nickname,
                        @NonNull AccountType accountType,
                        @NonNull String profileImage,
                        @NonNull LocalDateTime registerDate,
                        @NonNull Integer boardCount,
                        @NonNull Integer replyCount) {
        this.email = email;
        this.nickname = nickname;
        this.accountType = accountType;
        this.profileImage = profileImage;
        this.registerDate = registerDate;
        this.boardCount = boardCount;
        this.replyCount = replyCount;
    }
}
