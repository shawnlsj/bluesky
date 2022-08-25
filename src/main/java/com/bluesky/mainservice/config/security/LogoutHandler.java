package com.bluesky.mainservice.config.security;

import com.bluesky.mainservice.config.security.jwt.JwtMapper;
import com.bluesky.mainservice.service.user.LoginService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutHandler implements LogoutSuccessHandler {

    private final LoginService loginService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String refreshToken = JwtMapper.resolveRefreshToken(request);
        if (StringUtils.hasText(refreshToken)) {
            try {
                loginService.removeRefreshToken(refreshToken);
            } catch (JwtException | DataAccessException e) {
                log.info("remove refresh token failed", e);
            }
        }
        JwtMapper.removeAccessToken(response);
        JwtMapper.removeRefreshToken(response);
        response.sendRedirect("/");
    }
}
