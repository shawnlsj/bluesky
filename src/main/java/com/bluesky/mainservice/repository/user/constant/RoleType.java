package com.bluesky.mainservice.repository.user.constant;

public enum RoleType {
    ADMIN, ADMIN_PROGRAMMING, ADMIN_ENGLISH, ADMIN_TRAVEL, USER,;

    public static RoleType[] allAdmins() {
        return new RoleType[]{ADMIN, ADMIN_PROGRAMMING, ADMIN_PROGRAMMING, ADMIN_TRAVEL};
    }
}
