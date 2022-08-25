package com.bluesky.mainservice.repository.user;

import com.bluesky.mainservice.repository.user.constant.AccountType;
import com.bluesky.mainservice.repository.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByNickname(String nickname);

    @Query("select u.id from User u where u.uuid = :uuid")
    Optional<Long> findIdByUuid(@Param("uuid") UUID uuid);

    Optional<User> findByUuid(UUID uuid);

    Optional<User> findByEmailAndAccountType(String email, AccountType accountType);

    Optional<User> findBySocialLoginIdAndAccountType(String socialLoginId, AccountType accountType);
}
