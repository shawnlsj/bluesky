package com.bluesky.mainservice.service.user.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class LoginUserDetails {
    
    private final UUID userId;
    private final List<GrantedAuthority> authorities;
}
