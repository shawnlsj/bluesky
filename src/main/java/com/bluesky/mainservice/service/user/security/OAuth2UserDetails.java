package com.bluesky.mainservice.service.user.security;

import com.bluesky.mainservice.repository.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Builder
public class OAuth2UserDetails implements OAuth2User {

    @Getter(AccessLevel.PRIVATE)
    private OAuth2User oAuth2User;
    private User user;

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getName();
    }
}
