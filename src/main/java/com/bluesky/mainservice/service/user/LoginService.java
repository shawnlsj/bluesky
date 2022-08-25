package com.bluesky.mainservice.service.user;

import com.bluesky.mainservice.config.security.jwt.JwtGenerator;
import com.bluesky.mainservice.config.security.jwt.RefreshTokenParseData;
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

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class LoginService {

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
        RefreshToken refreshTokenEntity = new RefreshToken(tokenId, user);

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
        RefreshTokenParseData refreshTokenParseData = jwtGenerator.parseRefreshToken(refreshToken);
        UUID tokenId = refreshTokenParseData.getTokenId();

        //리프레시 토큰 조회
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUuid(tokenId);
        if (refreshTokenEntity == null) {
            return Optional.empty();
        }

        if (!refreshTokenEntity.isValid(refreshTokenParseData.getExpirationTimeMillis())) {
            return Optional.empty();
        }

        //액세스 토큰을 발급
        User user = refreshTokenEntity.getUser();
        String accessToken = jwtGenerator.generateAccessToken(user.getUuid(), user.isAdmin());
        return Optional.of(accessToken);
    }

    public void removeRefreshToken(String refreshToken) throws JwtException, DataAccessException {
        RefreshTokenParseData refreshTokenParseData = jwtGenerator.parseRefreshToken(refreshToken);
        UUID tokenId = refreshTokenParseData.getTokenId();
        refreshTokenRepository.deleteByUuid(tokenId);
    }
}
