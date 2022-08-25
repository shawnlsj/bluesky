package com.bluesky.mainservice.config.security.jwt;

import com.bluesky.mainservice.repository.user.constant.AccountType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MINUTES;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtGenerator {

    private final StringEncryptor encryptor;

    @Value("${jwt.secret}")
    private String secretKey;

    private static final String HEADER_TYP_KEY = "typ";
    private static final String HEADER_TYP_VALUE = "JWT";
    private static final String HEADER_ALG_KEY = "alg";
    private static final String HEADER_ALG_VALUE = "HS256";

    private static final String ISSUER = "vluesky.herokuapp.com";

    static final long ACCESS_TOKEN_EXPIRATION_TIME = MINUTES.toMillis(30);
    static final long REFRESH_TOKEN_EXPIRATION_TIME = DAYS.toMillis(14);
    static final long EMAIL_AUTH_TOKEN_EXPIRATION_TIME = MINUTES.toMillis(30);
    static final long RESET_PASSWORD_TOKEN_EXPIRATION_TIME = MINUTES.toMillis(30);

    //외부에서 주입받은 secret key 를 base64 로 인코딩 한다
    @PostConstruct
    private void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String generateAccessToken(UUID userId, boolean isAdmin) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME);

        Claims claims = Jwts.claims();
        claims.put(ClaimKey.TOKEN_TYPE, TokenType.ACCESS);
        claims.put(ClaimKey.USER_ID, userId);
        claims.put(ClaimKey.ADMIN, isAdmin);
        claims.setIssuer(ISSUER);
        claims.setIssuedAt(now);
        claims.setExpiration(expirationDate);

        return Jwts.builder()
                .setHeaderParam(HEADER_TYP_KEY, HEADER_TYP_VALUE)
                .setHeaderParam(HEADER_ALG_KEY, HEADER_ALG_VALUE)
                .setClaims(claims)
                .signWith(HS256, secretKey)
                .compact();
    }

    public String generateRefreshToken(UUID tokenId) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME);

        Claims claims = Jwts.claims();
        claims.put(ClaimKey.TOKEN_TYPE, TokenType.REFRESH);
        claims.put(ClaimKey.TOKEN_ID, tokenId);
        claims.setIssuer(ISSUER);
        claims.setIssuedAt(now);
        claims.setExpiration(expirationDate);
        claims.setId(String.valueOf(tokenId));

        return Jwts.builder()
                .setHeaderParam(HEADER_TYP_KEY, HEADER_TYP_VALUE)
                .setHeaderParam(HEADER_ALG_KEY, HEADER_ALG_VALUE)
                .setClaims(claims)
                .signWith(HS256, secretKey)
                .compact();
    }

    public String generateJoinToken(String userId, AccountType accountType) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EMAIL_AUTH_TOKEN_EXPIRATION_TIME);

        Claims claims = Jwts.claims();
        claims.put(ClaimKey.TOKEN_TYPE, TokenType.JOIN);
        claims.put(ClaimKey.ACCOUNT_TYPE, accountType.name());
        claims.put(ClaimKey.USER_ID, encryptor.encrypt(userId));
        claims.setIssuer(ISSUER);
        claims.setIssuedAt(now);
        claims.setExpiration(expirationDate);

        return Jwts.builder()
                .setHeaderParam(HEADER_TYP_KEY, HEADER_TYP_VALUE)
                .setHeaderParam(HEADER_ALG_KEY, HEADER_ALG_VALUE)
                .setClaims(claims)
                .signWith(HS256, secretKey)
                .compact();
    }

    public String generateResetPasswordToken(UUID userId) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + RESET_PASSWORD_TOKEN_EXPIRATION_TIME);

        Claims claims = Jwts.claims();
        claims.put(ClaimKey.TOKEN_TYPE, TokenType.RESET_PASSWORD);
        claims.put(ClaimKey.USER_ID, userId);
        claims.setIssuer(ISSUER);
        claims.setIssuedAt(now);
        claims.setExpiration(expirationDate);

        return Jwts.builder()
                .setHeaderParam(HEADER_TYP_KEY, HEADER_TYP_VALUE)
                .setHeaderParam(HEADER_ALG_KEY, HEADER_ALG_VALUE)
                .setClaims(claims)
                .signWith(HS256, secretKey)
                .compact();
    }

    public AccessTokenParseData parseAccessToken(String accessToken) {
        Claims body = getBody(accessToken);

        if (!checkTokenType(body, TokenType.ACCESS)) {
            throw new JwtException(TokenType.ACCESS + "토큰이 아닙니다.");
        }

        UUID userId = UUID.fromString(body.get(ClaimKey.USER_ID, String.class));
        boolean isAdmin = body.get(ClaimKey.ADMIN, Boolean.class);
        return new AccessTokenParseData(userId, isAdmin);
    }

    public RefreshTokenParseData parseRefreshToken(String refreshToken) {
        Claims body = getBody(refreshToken);

        if (!checkTokenType(body, TokenType.REFRESH)) {
            throw new JwtException(TokenType.REFRESH + "토큰이 아닙니다.");
        }

        UUID tokenId = UUID.fromString(body.get(ClaimKey.TOKEN_ID, String.class));
        Date expirationTime = body.getExpiration();
        return new RefreshTokenParseData(tokenId, expirationTime.getTime());
    }

    public JoinTokenParseData parseJoinToken(String joinToken) {
        Claims body = getBody(joinToken);

        if (!checkTokenType(body, TokenType.JOIN)) {
            throw new JwtException(TokenType.JOIN + "토큰이 아닙니다.");
        }

        String userId = encryptor.decrypt(body.get(ClaimKey.USER_ID, String.class));
        AccountType accountType = AccountType.valueOf(body.get(ClaimKey.ACCOUNT_TYPE, String.class));
        return new JoinTokenParseData(userId, accountType);
    }

    public ResetPasswordTokenParseData parseResetPasswordToken(String resetPasswordToken) {
        Claims body = getBody(resetPasswordToken);

        if (!checkTokenType(body, TokenType.RESET_PASSWORD)) {
            throw new JwtException(TokenType.RESET_PASSWORD + "토큰이 아닙니다.");
        }

        UUID userId = UUID.fromString(body.get(ClaimKey.USER_ID, String.class));
        return new ResetPasswordTokenParseData(userId);
    }

    private Claims getBody(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            log.info("invalid token", e);
            throw e;
        }
    }

    private boolean checkTokenType(Claims body, TokenType tokenType) {
        String value = body.get(ClaimKey.TOKEN_TYPE, String.class);
        return value.equals(tokenType.name());
    }

    public boolean isValid(String token, TokenType tokenType) {
        try {
            Claims body = getBody(token);
            if (checkTokenType(body, tokenType)) {
                return true;
            }
        } catch (JwtException ignored) {}
        return false;
    }

    private static class ClaimKey {
        static final String TOKEN_TYPE = "tokenType";
        static final String TOKEN_ID = "tokenId";
        static final String USER_ID = "userId";
        static final String ADMIN = "admin";
        static final String ACCOUNT_TYPE = "accountType";
    }
}
