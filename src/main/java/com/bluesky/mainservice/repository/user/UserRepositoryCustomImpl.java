package com.bluesky.mainservice.repository.user;

import com.bluesky.mainservice.repository.user.dto.QUserDto;
import com.bluesky.mainservice.repository.user.dto.UserDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.bluesky.mainservice.repository.user.domain.QUser.*;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<UserDto> findUserDto(Long userId) {
        UserDto userDto = queryFactory.select(new QUserDto(
                        user.email,
                        user.nickname,
                        user.accountType,
                        user.profileImage,
                        user.createdDate))
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();
        return Optional.ofNullable(userDto);
    }
}
