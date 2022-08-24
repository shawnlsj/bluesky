package com.bluesky.mainservice.repository.user;

import com.bluesky.mainservice.repository.user.dto.UserDto;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<UserDto> findUserDto(Long userId);
}
