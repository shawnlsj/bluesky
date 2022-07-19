package com.bluesky.mainservice.service.user;

import com.bluesky.mainservice.config.security.jwt.JoinTokenInfo;
import com.bluesky.mainservice.config.security.jwt.JwtGenerator;
import com.bluesky.mainservice.config.security.jwt.ResetPasswordTokenInfo;
import com.bluesky.mainservice.config.security.jwt.TokenType;
import com.bluesky.mainservice.repository.user.RoleRepository;
import com.bluesky.mainservice.repository.user.UserRepository;
import com.bluesky.mainservice.repository.user.UserRoleRepository;
import com.bluesky.mainservice.repository.user.constant.AccountType;
import com.bluesky.mainservice.repository.user.constant.RoleType;
import com.bluesky.mainservice.repository.user.domain.Role;
import com.bluesky.mainservice.repository.user.domain.User;
import com.bluesky.mainservice.repository.user.domain.UserRole;
import com.bluesky.mainservice.service.user.dto.LoginTokenSet;
import com.bluesky.mainservice.service.user.dto.JoinInProgressUserInfo;
import com.bluesky.mainservice.service.user.dto.UserAccountInfo;
import com.bluesky.mainservice.service.user.dto.UserRegisterData;
import com.bluesky.mainservice.service.user.exception.UserAlreadyRegisteredException;
import com.bluesky.mainservice.service.user.exception.UserNotFoundException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static com.bluesky.mainservice.repository.user.constant.AccountType.*;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final LoginService loginService;

    public LoginTokenSet register(UserRegisterData data, String joinToken) {

        if (!jwtGenerator.isValid(joinToken, TokenType.JOIN)) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        }

        //토큰에서 값을 가져옴
        JoinTokenInfo joinTokenInfo = jwtGenerator.parseJoinToken(joinToken);
        String userId = joinTokenInfo.getUserId();
        AccountType accountType = joinTokenInfo.getAccountType();

        //이메일 로그인 유저인지 확인
        //아니라면 소셜 로그인 유저로 프로세스 진행
        User user;
        if (data.isOriginalUser()) {
            //이메일 유저의 토큰이 아닐 경우 예외 발생
            if (accountType != ORIGINAL) {
                throw new JwtException("이메일 유저가 아닙니다.");
            }

            //등록된 유저인지 확인
            user = userRepository.findByEmailAndAccountType(userId, ORIGINAL);
            if (user != null) {
                throw new UserAlreadyRegisteredException("이미 등록된 사용자입니다.");
            }

            //유저를 DB 에 저장
            user = User.builder()
                    .email(userId)
                    .accountType(accountType)
                    .nickname(data.getNickname())
                    .password(passwordEncoder.encode(data.getPassword()))
                    .build();
            userRepository.save(user);

            //중간 테이블에 다대다 연관관계를 저장
            Role role = roleRepository.findByRoleType(RoleType.USER);
            userRoleRepository.save(new UserRole(user, role));
        } else {
            //소셜 로그인 유저가 아닐 경우 예외 발생
            if ((accountType != GOOGLE) && (accountType != KAKAO) && (accountType != NAVER)) {
                throw new JwtException("소셜 로그인 유저가 아닙니다.");
            }

            //유저를 데이터베이스에서 조회
            user = userRepository.findBySocialLoginIdAndAccountType(userId, accountType);

            //닉네임을 가지고 있으면 예외 발생
            if (StringUtils.hasText(user.getNickname())) {
                throw new UserAlreadyRegisteredException("이미 등록된 사용자입니다.");
            }

            //닉네임 정보를 업데이트
            user.updateNickName(data.getNickname());
        }
        //로그인용 토큰을 발급
        return loginService.issueLoginTokenSet(user);
    }

    @Transactional(readOnly = true)
    public JoinInProgressUserInfo createJoinInProgressUserInfo(String joinToken) {

        //토큰이 유효하지 않을 경우 예외 발생
        if (!jwtGenerator.isValid(joinToken, TokenType.JOIN)) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        }

        JoinTokenInfo joinTokenInfo = jwtGenerator.parseJoinToken(joinToken);
        AccountType accountType = joinTokenInfo.getAccountType();
        String email;
        User user;
        if (accountType == ORIGINAL) {
            email = joinTokenInfo.getUserId();
            user = userRepository.findByEmailAndAccountType(email, accountType);
        } else {
            //소셜 로그인 유저인데 조회된 결과가 없으면 예외 발생
            user = userRepository.findBySocialLoginIdAndAccountType(joinTokenInfo.getUserId(), accountType);
            if (user == null) {
                throw new UserNotFoundException("존재하지 않는 사용자입니다.");
            }
            email = user.getEmail();
        }

        //이미 등록된 사용자일 경우 예외 발생
        if ((user != null) && user.isRegisteredUser()) {
            throw new UserAlreadyRegisteredException("이미 등록된 사용자입니다.");
        }

        return new JoinInProgressUserInfo(email, accountType);
    }

    public UserAccountInfo createUserAccountInfo(UUID userId) {
        User user = userRepository.findByUuid(userId);
        if (user == null) {
            throw new UserNotFoundException("존재하지 않는 사용자입니다.");
        }

        return UserAccountInfo.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .accountType(user.getAccountType())
                .registerDate(user.getCreatedDate())
                .build();

    }

    public void resetPassword(String resetPasswordToken, String password) {
        //토큰 유효성 검사
        if (!jwtGenerator.isValid(resetPasswordToken, TokenType.RESET_PASSWORD)) {
            throw new JwtException("유효한 토큰이 아닙니다.");
        }

        //조회되는 유저가 없으면 예외 발생
        ResetPasswordTokenInfo resetPasswordTokenInfo = jwtGenerator.parseResetPasswordToken(resetPasswordToken);
        UUID userId = resetPasswordTokenInfo.getUserId();
        User user = userRepository.findByUuid(userId);
        if (user == null) {
            throw new UserNotFoundException("존재하지 않는 사용자 입니다.");
        }

        //비밀번호 업데이트
        user.updatePassword(passwordEncoder.encode(password));
    }
}
