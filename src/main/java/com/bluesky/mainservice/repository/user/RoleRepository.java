package com.bluesky.mainservice.repository.user;

import com.bluesky.mainservice.repository.user.constant.RoleType;
import com.bluesky.mainservice.repository.user.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRoleType(RoleType roleType);
}
