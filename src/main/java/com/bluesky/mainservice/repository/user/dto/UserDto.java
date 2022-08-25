package com.bluesky.mainservice.repository.user.dto;

import com.bluesky.mainservice.repository.user.constant.AccountType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserDto {
    private String email;
    private String nickname;
    private AccountType accountType;
    private String profileImage;
    private LocalDateTime createdDated;

    @QueryProjection
    public UserDto(String email,
                   String nickname,
                   AccountType accountType,
                   String profileImage,
                   LocalDateTime createdDated) {
        this.email = email;
        this.nickname = nickname;
        this.accountType = accountType;
        this.profileImage = profileImage;
        this.createdDated = createdDated;
    }
}
