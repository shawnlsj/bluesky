package com.bluesky.mainservice.repository.user;

import com.bluesky.mainservice.repository.user.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    RefreshToken findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);
}
