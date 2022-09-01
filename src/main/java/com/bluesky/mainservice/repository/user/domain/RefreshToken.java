package com.bluesky.mainservice.repository.user.domain;

import com.bluesky.mainservice.repository.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;

@Getter
@Entity
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseTimeEntity {

    public static final long REFRESH_CYCLE_MONITOR_PERIOD = DAYS.toMillis(3);
    public static final long ALLOWED_REFRESH_CYCLE = HOURS.toMillis(4);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @NotNull
    @Column(columnDefinition = "binary(16)", unique = true)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "last_refresh_date")
    private LocalDateTime lastRefreshDate;

    public RefreshToken(UUID tokenId, User user) {
        this.uuid = tokenId;
        this.user = user;
    }

    public void refresh() {
        lastRefreshDate = LocalDateTime.now();
    }

    public boolean isValid(long expirationTimeMillis) {
        //남은 시간 확인
        long lastRefreshDateMillis = lastRefreshDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        //토큰의 남은 만료 시간이 모니터링 기간안에 포함되는지 확인
        if (expirationTimeMillis - now <= REFRESH_CYCLE_MONITOR_PERIOD) {
            //토큰 갱신 주기가 허용된 주기를 초과하였으면 false 반환
            return now - lastRefreshDateMillis <= ALLOWED_REFRESH_CYCLE;
        }
        return true;
    }
}
