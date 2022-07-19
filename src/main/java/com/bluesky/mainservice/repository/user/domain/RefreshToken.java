package com.bluesky.mainservice.repository.user.domain;

import com.bluesky.mainservice.repository.BaseTimeEntity;
import com.bluesky.mainservice.repository.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

import static javax.persistence.FetchType.*;

@Getter
@Entity
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseTimeEntity {

    @Builder
    private RefreshToken(UUID uuid, User user, String value) {
        this.uuid = uuid;
        this.user = user;
        this.value = value;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @Column(columnDefinition = "binary(16)", unique = true)
    private UUID uuid;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(length = 2048)
    private String value;

    @Column(name = "last_refresh_date")
    private LocalDateTime lastRefreshDate;

    public void refresh() {
        lastRefreshDate = LocalDateTime.now();
    }
}
