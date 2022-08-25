package com.bluesky.mainservice.config.filter;

import com.bluesky.mainservice.config.security.jwt.JwtAuthenticationProvider;
import com.bluesky.mainservice.config.security.jwt.JwtGenerator;
import com.bluesky.mainservice.config.security.jwt.JwtMapper;
import com.bluesky.mainservice.service.user.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.bluesky.mainservice.config.security.jwt.TokenType.ACCESS;
import static com.bluesky.mainservice.config.security.jwt.TokenType.REFRESH;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final JwtGenerator jwtGenerator;
    private final LoginService loginService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = JwtMapper.resolveAccessToken(request);
        String refreshToken = JwtMapper.resolveRefreshToken(request);

        //유효한 액세스 토큰이면 인증 진행
        //유효하지 않으면 리프레시 토큰으로 액세스 토큰 발급 시도
        if (StringUtils.hasText(accessToken) && jwtGenerator.isValid(accessToken, ACCESS)) {
            SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationProvider.authenticate(accessToken));

            //리프레시 토큰으로 액세스 토큰이 발급 되었으면 인증 진행
            //발급이 안되었으면 토큰을 삭제
        } else if (StringUtils.hasText(refreshToken) && jwtGenerator.isValid(refreshToken, REFRESH)) {
            loginService.refreshAccessToken(refreshToken).ifPresentOrElse(
                    refreshedAccessToken -> {
                        SecurityContextHolder.getContext()
                                .setAuthentication(jwtAuthenticationProvider.authenticate(refreshedAccessToken));
                        JwtMapper.addAccessToken(response, refreshedAccessToken);
                    }, () -> {
                        JwtMapper.removeAccessToken(response);
                        JwtMapper.removeRefreshToken(response);
                    });
        }
        doFilter(request, response, filterChain);
    }
}
