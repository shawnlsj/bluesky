package com.bluesky.mainservice.unit;

import com.bluesky.mainservice.repository.user.domain.RefreshToken;
import com.bluesky.mainservice.repository.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static com.bluesky.mainservice.repository.user.domain.RefreshToken.REFRESH_CYCLE_MONITOR_PERIOD;
import static java.util.concurrent.TimeUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenValidationTest {

    @Mock
    private User user;

    @Mock
    private UUID tokenId;

    private RefreshToken refreshToken;

    @Nested
    @DisplayName("남은 시간이 모니터링 기간안에 포함되지 않을 때")
    class when_remain_time_not_in_monitor_period {

        long expirationTimeMillis;

        @BeforeEach
        void init() {
            long now = new Date().getTime();
            expirationTimeMillis = now + REFRESH_CYCLE_MONITOR_PERIOD + DAYS.toMillis(14);
            refreshToken = new RefreshToken(tokenId, user);
        }

        @Test
        @DisplayName("리프레시 주기가 허용 시간을 넘지 않으면 true")
        void if_not_over_allowed_refresh_cycle_return_true() {
            //given
            refreshToken.refresh();

            //when
            boolean result = refreshToken.isValid(expirationTimeMillis);

            //then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("리프레시 주기가 허용 시간을 넘어도 true")
        void if_over_allowed_refresh_cycle_return_true() {
            //given
            LocalDateTime lastRefreshDate = LocalDateTime.now().minusDays(1);
            try (MockedStatic<LocalDateTime> localDateTime = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTime.when(LocalDateTime::now).thenReturn(lastRefreshDate);
                refreshToken.refresh();
            }

            //when
            boolean result = refreshToken.isValid(expirationTimeMillis);

            //then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("남은 시간이 모니터링 기간안에 포함될 때")
    class when_remain_time_in_monitor_period {

        long expirationTimeMillis;

        @BeforeEach
        void init() {
            long now = new Date().getTime();
            expirationTimeMillis = now + REFRESH_CYCLE_MONITOR_PERIOD;
            refreshToken = new RefreshToken(tokenId, user);
        }

        @DisplayName("허용된 리프레시 주기를 넘지 않으면 true")
        @Test
        void if_not_over_allowed_refresh_cycle_return_true() {
            //given
            refreshToken.refresh();

            //when
            boolean result = refreshToken.isValid(expirationTimeMillis);

            //then
            assertThat(result).isTrue();
        }

        @DisplayName("허용된 리프레시 주기를 넘으면 false")
        @Test
        void if_over_allowed_refresh_cycle_return_false() {
            //given
            LocalDateTime lastRefreshDate = LocalDateTime.now().minusDays(1);
            try (MockedStatic<LocalDateTime> localDateTime = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTime.when(LocalDateTime::now).thenReturn(lastRefreshDate);
                refreshToken.refresh();
            }

            //when
            boolean result = refreshToken.isValid(expirationTimeMillis);

            //then
            assertThat(result).isFalse();
        }
    }
}
