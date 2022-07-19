package com.bluesky.mainservice.service.user.dto;

import com.bluesky.mainservice.repository.user.constant.AccountType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자의 계정 정보를 포함합니다.
 */
@Builder
@Getter
public class UserAccountInfo {
    String email;
    String nickname;
    AccountType accountType;
    LocalDateTime registerDate;
}
