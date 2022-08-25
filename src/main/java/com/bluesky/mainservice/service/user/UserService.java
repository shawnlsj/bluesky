package com.bluesky.mainservice.service.user;

import com.bluesky.mainservice.config.security.jwt.JoinTokenParseData;
import com.bluesky.mainservice.config.security.jwt.JwtGenerator;
import com.bluesky.mainservice.config.security.jwt.ResetPasswordTokenParseData;
import com.bluesky.mainservice.config.security.jwt.TokenType;
import com.bluesky.mainservice.repository.community.board.BoardRepository;
import com.bluesky.mainservice.repository.community.board.ReplyRepository;
import com.bluesky.mainservice.repository.user.RoleRepository;
import com.bluesky.mainservice.repository.user.UserRepository;
import com.bluesky.mainservice.repository.user.UserRoleRepository;
import com.bluesky.mainservice.repository.user.constant.AccountType;
import com.bluesky.mainservice.repository.user.constant.RoleType;
import com.bluesky.mainservice.repository.user.domain.Role;
import com.bluesky.mainservice.repository.user.domain.User;
import com.bluesky.mainservice.repository.user.domain.UserRole;
import com.bluesky.mainservice.repository.user.dto.UserDto;
import com.bluesky.mainservice.service.user.dto.JoinInProgressUser;
import com.bluesky.mainservice.service.user.dto.LoginTokenSet;
import com.bluesky.mainservice.service.user.dto.NewUser;
import com.bluesky.mainservice.service.user.dto.UserProfile;
import com.bluesky.mainservice.service.user.exception.UserAlreadyRegisteredException;
import com.bluesky.mainservice.service.user.exception.UserNotFoundException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Random;
import java.util.UUID;

import static com.bluesky.mainservice.repository.user.constant.AccountType.*;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final LoginService loginService;

    public LoginTokenSet register(NewUser newUser, String joinToken) {

        if (!jwtGenerator.isValid(joinToken, TokenType.JOIN)) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        }

        //토큰에서 값을 가져옴
        JoinTokenParseData joinTokenParseData = jwtGenerator.parseJoinToken(joinToken);
        String userId = joinTokenParseData.getUserId();
        AccountType accountType = joinTokenParseData.getAccountType();

        //이메일 로그인 유저인지 확인
        //아니라면 소셜 로그인 유저로 프로세스 진행
        User user;
        if (newUser.isOriginalUser()) {
            //이메일 유저의 토큰이 아닐 경우 예외 발생
            if (accountType != ORIGINAL) {
                throw new JwtException("이메일 유저가 아닙니다.");
            }

            //등록된 유저인지 확인
            userRepository
                    .findByEmailAndAccountType(userId, ORIGINAL)
                    .ifPresent(registeredUser ->
                    {throw new UserAlreadyRegisteredException();});

            //유저를 DB 에 저장
            user = User.builder()
                    .email(userId)
                    .accountType(accountType)
                    .nickname(newUser.getNickname())
                    .password(passwordEncoder.encode(newUser.getPassword()))
                    .build();
            userRepository.save(user);

            //중간 테이블에 다대다 연관관계를 저장
            Role role = roleRepository.findByRoleType(RoleType.USER);
            UserRole userRole = new UserRole(user, role);
            userRoleRepository.save(userRole);

            //영속성 컨텍스트에 있는 엔티티에 권한정보 추가
            user.getUserRoles().add(userRole);
        } else {
            //소셜 로그인 유저가 아닐 경우 예외 발생
            if ((accountType != GOOGLE) && (accountType != KAKAO) && (accountType != NAVER)) {
                throw new JwtException("소셜 로그인 유저가 아닙니다.");
            }

            //유저를 데이터베이스에서 조회
            user = userRepository.findBySocialLoginIdAndAccountType(userId, accountType)
                    .orElseThrow(UserNotFoundException::new);

            //닉네임을 가지고 있으면 예외 발생
            if (StringUtils.hasText(user.getNickname())) {
                throw new UserAlreadyRegisteredException();
            }

            //닉네임 정보를 업데이트
            user.updateNickName(newUser.getNickname());
        }
        //프로필 이미지를 기본 이미지 중 하나로 업데이트
        String profileImage = (new Random().nextInt(9) + 1) + ".jpg";
        user.updateProfileImage(profileImage);

        //로그인용 토큰을 발급
        return loginService.issueLoginTokenSet(user);
    }

    @Transactional(readOnly = true)
    public JoinInProgressUser createJoinInProgressUserInfo(String joinToken) {

        //토큰이 유효하지 않을 경우 예외 발생
        if (!jwtGenerator.isValid(joinToken, TokenType.JOIN)) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        }

        JoinTokenParseData joinTokenParseData = jwtGenerator.parseJoinToken(joinToken);
        AccountType accountType = joinTokenParseData.getAccountType();
        String email;
        User user;
        if (accountType == ORIGINAL) {
            email = joinTokenParseData.getUserId();
            user = userRepository.findByEmailAndAccountType(email, accountType)
                    .orElse(null);
        } else {
            //소셜 로그인 유저인데 조회된 결과가 없으면 예외 발생
            user = userRepository.findBySocialLoginIdAndAccountType(joinTokenParseData.getUserId(), accountType)
                    .orElseThrow(UserNotFoundException::new);
            email = user.getEmail();
        }

        //이미 등록된 사용자일 경우 예외 발생
        if ((user != null) && user.isRegisteredUser()) {
            throw new UserAlreadyRegisteredException();
        }

        return new JoinInProgressUser(email, accountType);
    }

    public void resetPassword(String resetPasswordToken, String password) {
        //토큰 유효성 검사
        if (!jwtGenerator.isValid(resetPasswordToken, TokenType.RESET_PASSWORD)) {
            throw new JwtException("유효한 토큰이 아닙니다.");
        }

        //조회되는 유저가 없으면 예외 발생
        ResetPasswordTokenParseData resetPasswordTokenParseData = jwtGenerator.parseResetPasswordToken(resetPasswordToken);
        UUID userId = resetPasswordTokenParseData.getUserId();
        User user = userRepository.findByUuid(userId)
                .orElseThrow(UserNotFoundException::new);

        //비밀번호 업데이트
        user.updatePassword(passwordEncoder.encode(password));
    }

    public UserProfile findUserProfile(UUID userId) {
        Long id = userRepository.findIdByUuid(userId)
                .orElseThrow(UserNotFoundException::new);

        UserDto userDto = userRepository.findUserDto(id)
                .orElseThrow(UserNotFoundException::new);

        int boardCount = boardRepository.countActiveUserBoard(id);
        int replyCount = replyRepository.countActiveUserReply(id);

       return UserProfile.builder()
                .nickname(userDto.getNickname())
                .email(userDto.getEmail())
                .profileImage(userDto.getProfileImage())
                .registerDate(userDto.getCreatedDated())
                .accountType(userDto.getAccountType())
                .boardCount(boardCount)
                .replyCount(replyCount)
                .build();
    }
}
