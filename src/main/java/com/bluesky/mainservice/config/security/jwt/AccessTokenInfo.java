package com.bluesky.mainservice.config.security.jwt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class AccessTokenInfo {
    private UUID userId;
    private boolean isAdmin;
}
