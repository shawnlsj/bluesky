package com.bluesky.mainservice.config.security.jwt;

import com.bluesky.mainservice.service.user.security.LoginUserDetails;
import com.bluesky.mainservice.service.user.security.UserDetailsLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {
    private final UserDetailsLoader userDetailsLoader;
    private final JwtGenerator jwtGenerator;

    public Authentication authenticate(String accessToken) {
        AccessTokenInfo accessTokenInfo = jwtGenerator.parseAccessToken(accessToken);

        Optional<LoginUserDetails> loginUserDetails = userDetailsLoader
                .loadLoginUserByUuid(accessTokenInfo.getUserId(), accessTokenInfo.isAdmin());

        if (loginUserDetails.isEmpty()) {
            return null;
        } else {
            return new UsernamePasswordAuthenticationToken
                    (loginUserDetails.get(), "", loginUserDetails.get().getAuthorities());
        }
    }
}
