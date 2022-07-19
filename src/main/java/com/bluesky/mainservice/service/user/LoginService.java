package com.bluesky.mainservice.service.user;

import com.bluesky.mainservice.config.security.jwt.JwtGenerator;
import com.bluesky.mainservice.config.security.jwt.RefreshTokenInfo;
import com.bluesky.mainservice.config.security.jwt.TokenType;
import com.bluesky.mainservice.repository.user.RefreshTokenRepository;
import com.bluesky.mainservice.repository.user.domain.RefreshToken;
import com.bluesky.mainservice.repository.user.domain.User;
import com.bluesky.mainservice.service.user.dto.LoginTokenSet;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;

@RequiredArgsConstructor
@Transactional
@Service
public class LoginService {

    private static final long REFRESH_CYCLE_MONITOR_PERIOD = DAYS.toMillis(3);
    private static final long ALLOWED_REFRESH_CYCLE = HOURS.toMillis(4);

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtGenerator jwtGenerator;

    public LoginTokenSet issueLoginTokenSet(User user) {
        UUID tokenId = UUID.randomUUID();
        UUID userId = user.getUuid();
        boolean isAdmin = user.isAdmin();

        //토큰 생성
        String accessToken = jwtGenerator.generateAccessToken(userId, isAdmin);
        String refreshToken = jwtGenerator.generateRefreshToken(tokenId);

        //리프레시 토큰 엔티티 생성
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .uuid(tokenId)
                .user(user)
                .value(refreshToken)
                .build();

        //최근 토큰 발급 날짜를 갱신
        refreshTokenEntity.refresh();

        refreshTokenRepository.save(refreshTokenEntity);
        return new LoginTokenSet(accessToken, refreshToken);
    }

    public Optional<String> refreshAccessToken(String refreshToken) {
        if (!jwtGenerator.isValid(refreshToken, TokenType.REFRESH)) {
            return Optional.empty();
        }

        //리프레시 토큰을 파싱하고 토큰 id 를 획득
        RefreshTokenInfo refreshTokenInfo = jwtGenerator.parseRefreshToken(refreshToken);
        UUID tokenId = refreshTokenInfo.getTokenId();

        //리프레시 토큰 조회
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUuid(tokenId);
        if (refreshTokenEntity == null) {
            return Optional.empty();
        }

        //남은 시간 확인
        LocalDateTime lastRefreshDate = refreshTokenEntity.getLastRefreshDate();
        long lastRefreshDateMillis = lastRefreshDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long expirationTimeMillis = refreshTokenInfo.getExpirationTimeMillis();
        long now = System.currentTimeMillis();

        //토큰의 남은 만료 시간이 모니터링 기간안에 포함되는지 확인
        if (expirationTimeMillis - now <= REFRESH_CYCLE_MONITOR_PERIOD) {
            //토큰 갱신 주기가 허용된 주기를 초과하였으면 토큰을 삭제하고 빈 값을 반환
            if (now - lastRefreshDateMillis > ALLOWED_REFRESH_CYCLE) {
                refreshTokenRepository.delete(refreshTokenEntity);
                return Optional.empty();
            }
        }

        //액세스 토큰을 발급
        User user = refreshTokenEntity.getUser();
        String accessToken = jwtGenerator.generateAccessToken(user.getUuid(), user.isAdmin());
        return Optional.of(accessToken);
    }

    public void removeRefreshToken(String refreshToken) throws JwtException, DataAccessException {
        RefreshTokenInfo refreshTokenInfo = jwtGenerator.parseRefreshToken(refreshToken);
        UUID tokenId = refreshTokenInfo.getTokenId();
        refreshTokenRepository.deleteByUuid(tokenId);
    }
}
