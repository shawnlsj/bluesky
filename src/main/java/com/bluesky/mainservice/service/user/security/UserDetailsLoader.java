package com.bluesky.mainservice.service.user.security;

import com.bluesky.mainservice.repository.user.UserRepository;
import com.bluesky.mainservice.repository.user.constant.AccountType;
import com.bluesky.mainservice.repository.user.constant.RoleType;
import com.bluesky.mainservice.repository.user.domain.User;
import com.bluesky.mainservice.repository.user.domain.UserRole;
import com.bluesky.mainservice.service.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserDetailsLoader implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndAccountType(username, AccountType.ORIGINAL);
        return entityToUserDetails(user);
    }

    public Optional<LoginUserDetails> loadLoginUserByUuid(UUID userId, boolean isAdmin) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (isAdmin) {
            User user = userRepository.findByUuid(userId);

            //조회된 사용자가 없을 경우 예외 발생
            if (user == null) {
                throw new UserNotFoundException("존재하지 않는 사용자입니다.");
            }

            //현재 관리자가 아닌 사용자라면 비어있는 Optional 객체를 리턴
            if (!user.isAdmin()) {
                return Optional.empty();
            }

            //유저가 보유하고 있는 권한들을 리스트에 담음
            grantAuthorities(user, authorities);

        } else {
            //관리자가 아니면 USER 권한을 부여
            authorities.add(new SimpleGrantedAuthority(RoleType.USER.name()));
        }

        //principal 생성
        LoginUserDetails loginUserDetails = new LoginUserDetails(userId, authorities);
        return Optional.of(loginUserDetails);
    }

    private UserDetails entityToUserDetails(User user) {
        //유저를 조회하고 존재하는 유저인지 확인
        if (user == null) {
            throw new UsernameNotFoundException("존재하지 않는 계정입니다.");
        }

        //유저가 보유하고 있는 권한들을 리스트에 담음
        List<GrantedAuthority> authorities = new ArrayList<>();
        grantAuthorities(user, authorities);

        //UserDetails 구현체 생성
        return OriginalAuthUserDetails.builder()
                .user(user)
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

    private void grantAuthorities(User user, List<GrantedAuthority> authorities) {
        List<UserRole> userRoles = user.getUserRoles();
        for (UserRole userRole : userRoles) {
            RoleType roleType = userRole.getRole().getRoleType();
            authorities.add(new SimpleGrantedAuthority(roleType.name()));
        }
    }
}
