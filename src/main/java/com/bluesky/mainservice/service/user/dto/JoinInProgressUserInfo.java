package com.bluesky.mainservice.service.user.dto;

import com.bluesky.mainservice.repository.user.constant.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 회원가입 진행중인 사용자에 대한 정보를 포함합니다.
 */
@AllArgsConstructor
@Getter
public class JoinInProgressUserInfo {
    String email;
    AccountType accountType;
}
