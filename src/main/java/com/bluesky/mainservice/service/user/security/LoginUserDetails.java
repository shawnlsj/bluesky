package com.bluesky.mainservice.service.user.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class LoginUserDetails {
    private UUID userId;
    private List<GrantedAuthority> authorities;
}
