package com.bluesky.mainservice.repository.user;

import com.bluesky.mainservice.repository.user.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
