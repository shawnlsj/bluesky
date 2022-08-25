package com.bluesky.mainservice.service.user.security;

import com.bluesky.mainservice.repository.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Getter
public class OriginalAuthUserDetails implements UserDetails {

    private final User user;
    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;

    @Builder
    private OriginalAuthUserDetails(@NonNull User user,
                                    @NonNull String username,
                                    @NonNull String password,
                                    @NonNull List<GrantedAuthority> authorities) {
        this.user = user;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
