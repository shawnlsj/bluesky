package com.bluesky.mainservice.repository.user;

import com.bluesky.mainservice.repository.user.constant.AccountType;
import com.bluesky.mainservice.repository.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    User findByNickname(String nickname);

    User findByUuid(UUID uuid);

    User findByEmailAndAccountType(String email, AccountType accountType);

    User findBySocialLoginIdAndAccountType(String socialLoginId, AccountType accountType);
}
