package com.bluesky.mainservice.config.security.jwt;

import com.bluesky.mainservice.repository.user.constant.AccountType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class JoinTokenParseData {
    private final String userId;
    private final AccountType accountType;
}
