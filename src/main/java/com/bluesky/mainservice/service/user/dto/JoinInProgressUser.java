package com.bluesky.mainservice.service.user.dto;

import com.bluesky.mainservice.repository.user.constant.AccountType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * 회원가입 진행중인 사용자에 대한 정보를 포함합니다.
 */
@RequiredArgsConstructor
@Getter
public class JoinInProgressUser {

    private final String email;
    private final AccountType accountType;
}
