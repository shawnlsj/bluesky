package com.bluesky.mainservice.repository.user;

import com.bluesky.mainservice.repository.user.constant.RoleType;
import com.bluesky.mainservice.repository.user.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query("select r.id from Role r where r.roleType = :roleType")
    Integer findIdByRoleType(@Param("roleType") RoleType roleType);

    Role findByRoleType(RoleType roleType);
}
