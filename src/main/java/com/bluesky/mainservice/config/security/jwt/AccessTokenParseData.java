package com.bluesky.mainservice.config.security.jwt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class AccessTokenParseData {
    private final UUID userId;
    private final boolean isAdmin;
}
