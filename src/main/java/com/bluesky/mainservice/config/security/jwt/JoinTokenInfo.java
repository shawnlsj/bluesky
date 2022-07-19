package com.bluesky.mainservice.config.security.jwt;

import com.bluesky.mainservice.repository.user.constant.AccountType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class JoinTokenInfo {
    private String userId;
    private AccountType accountType;
}
