package com.bluesky.mainservice.controller.user;

import com.bluesky.mainservice.config.security.jwt.JwtGenerator;
import com.bluesky.mainservice.config.security.jwt.JwtMapper;
import com.bluesky.mainservice.config.security.jwt.ResetPasswordTokenParseData;
import com.bluesky.mainservice.config.security.jwt.TokenType;
import com.bluesky.mainservice.controller.argument.LoginUser;
import com.bluesky.mainservice.controller.user.dto.OAuth2UserSaveForm;
import com.bluesky.mainservice.controller.user.dto.PasswordParam;
import com.bluesky.mainservice.controller.user.dto.UserResponseDto;
import com.bluesky.mainservice.controller.user.dto.UserSaveForm;
import com.bluesky.mainservice.repository.user.constant.AccountType;
import com.bluesky.mainservice.service.user.UserService;
import com.bluesky.mainservice.service.user.dto.JoinInProgressUser;
import com.bluesky.mainservice.service.user.dto.LoginTokenSet;
import com.bluesky.mainservice.service.user.dto.NewUser;
import com.bluesky.mainservice.service.user.dto.UserProfile;
import com.bluesky.mainservice.service.user.exception.UserAlreadyRegisteredException;
import com.bluesky.mainservice.service.user.exception.UserNotFoundException;
import com.bluesky.mainservice.util.CookieUtils;
import com.bluesky.mainservice.util.RegexUtils;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;
    private final JwtGenerator jwtGenerator;

    @GetMapping("/users/{userId:[^.]+}")
    public String profilePage(@PathVariable UUID userId,
                              LoginUser loginUser,
                              Model model) {
        UserProfile userProfile = userService.findUserProfile(userId);

        //조회 대상 유저가 로그인한 유저 자기 자신인지 확인
        boolean isMyself;
        if (loginUser == null) {
            isMyself = false;
        } else {
            isMyself = userId.equals(loginUser.getId());
        }
        UserResponseDto.UserProfile profile = UserResponseDto.UserProfile.builder()
                .profileImage(userProfile.getProfileImage())
                .nickname(userProfile.getNickname())
                .boardCount(userProfile.getBoardCount())
                .replyCount(userProfile.getReplyCount())
                .isMyself(isMyself)
                .build();


        model.addAttribute("profile", profile);
        return "user/profile";
    }

    @GetMapping("/mypage")
    public String mypage(LoginUser loginUser, Model model) {
        //사용자 정보를 조회
        UserProfile userProfile = userService.findUserProfile(loginUser.getId());

        //외부에 노출할 이메일을 마스킹
        String maskedEmail = RegexUtils.maskingEmail(userProfile.getEmail());

        //어떤 방법으로 가입했는지를 문자열로 변환
        String accountType = "";
        switch (userProfile.getAccountType()) {
            case ORIGINAL:
                accountType = "이메일 회원가입";
                break;
            case GOOGLE:
                accountType = "간편가입 (구글)";
                break;
            case KAKAO:
                accountType = "간편가입 (카카오)";
                break;
            case NAVER:
                accountType = "간편가입 (네이버)";
                break;
        }

        //dto 변환
        UserResponseDto.MyInformation myInformation = UserResponseDto.MyInformation.builder()
                .profileImage(userProfile.getProfileImage())
                .maskedEmail(maskedEmail)
                .nickname(userProfile.getNickname())
                .accountType(accountType)
                .joinDate(userProfile.getRegisterDate())
                .boardCount(userProfile.getBoardCount())
                .replyCount(userProfile.getReplyCount())
                .build();

        model.addAttribute("myInformation", myInformation);
        return "user/mypage_main";
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String originUrl, HttpServletRequest request, HttpServletResponse response) {

        //2가지 조건을 확인하여 쿠키에 리다이렉트 주소를 저장
        //1. 원래 요청하려했던 주소(originUrl) 를 쿼리스트링으로 가지고 있음
        //2. 그 주소는 서버와 도메인이 같으면서 https 를 사용하고 있음
        if (StringUtils.hasText(originUrl) && originUrl.startsWith("https://" + request.getServerName())) {
            Cookie cookie = CookieUtils.createRedirectCookie(originUrl);
            response.addCookie(cookie);
        }
        return "user/login_form";
    }

    //로그인 실패 시 포워딩 여기로 포워딩 됨
    @PostMapping("/login")
    public String failedLoginForm(@RequestParam String username, Model model) {
        model.addAttribute("email", username);
        return "user/login_form";
    }

    @GetMapping("/join")
    public String joinForm(@RequestParam(required = false) String token, Model model) {

        //토큰이 없으면 처음 화면으로 이동한다
        if (!StringUtils.hasText(token)) {
            return "user/join_verification";
        }

        //가입하려는 유저의 정보를 가져온다
        JoinInProgressUser joinInProgressUser = userService.createJoinInProgressUserInfo(token);

        //이메일 가입 유저인 경우 시간 연장을 위해 토큰을 다시 발급
        boolean isOriginalUser = (joinInProgressUser.getAccountType() == AccountType.ORIGINAL);
        if (isOriginalUser) {
            token = jwtGenerator.generateJoinToken(joinInProgressUser.getEmail(), AccountType.ORIGINAL);
        }

        //화면에서 사용할 정보를 담는다
        String maskedEmail = RegexUtils.maskingEmail(joinInProgressUser.getEmail());
        UserResponseDto.JoinFormAttribute joinFormAttribute = new UserResponseDto.JoinFormAttribute(token, maskedEmail, isOriginalUser);
        model.addAttribute("joinFormAttribute", joinFormAttribute);
        model.addAttribute("userSaveForm", new UserSaveForm());
        return "user/join_form";
    }

    @GetMapping("/join/complete")
    public String joinComplete(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        //쿠키에서 가입 완료된 사용자의 닉네임을 획득
        String nickname = CookieUtils.resolveMessageCookie(request);

        //저장되어 있는 닉네임이 없는 경우 404 예외를 던짐
        if (!StringUtils.hasText(nickname)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        model.addAttribute("nickname", nickname);
        CookieUtils.removeMessageCookie(response, "/join/complete");
        return "user/join_complete";
    }

    @PostMapping("/join")
    public String join(@Valid UserSaveForm userSaveForm,
                       @RequestParam String token,
                       HttpServletResponse response) {
        //서비스에 넘겨줄 dto 생성
        NewUser data = NewUser.builder()
                .nickname(userSaveForm.getNickname())
                .password(userSaveForm.getPassword())
                .isOriginalUser(true)
                .build();

        //유저를 등록하고, 발급받은 로그인용 토큰을 응답에 추가
        LoginTokenSet loginTokenSet = userService.register(data, token);
        JwtMapper.addAccessToken(response, loginTokenSet.getAccessToken());
        JwtMapper.addRefreshToken(response, loginTokenSet.getRefreshToken());

        //가입 완료된 닉네임을 쿠키에 저장
        response.addCookie(CookieUtils.createMessageCookie(userSaveForm.getNickname(), "/join/complete"));
        return "redirect:/join/complete";
    }

    @PostMapping("/join/oauth2")
    public String join(@Valid OAuth2UserSaveForm joinForm,
                       @RequestParam String token,
                       HttpServletResponse response) {
        //서비스에 넘겨줄 dto 생성
        NewUser data = NewUser.builder()
                .nickname(joinForm.getNickname())
                .isOriginalUser(false)
                .build();

        //유저를 등록하고, 발급받은 로그인용 토큰을 응답에 추가
        LoginTokenSet loginTokenSet = userService.register(data, token);
        JwtMapper.addAccessToken(response, loginTokenSet.getAccessToken());
        JwtMapper.addRefreshToken(response, loginTokenSet.getRefreshToken());

        //가입 완료된 닉네임을 쿠키에 저장
        response.addCookie(CookieUtils.createMessageCookie(joinForm.getNickname(), "/join/complete"));
        return "redirect:/join/complete";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam(required = false) String token, Model model) {
        //토큰이 없으면 이메일 인증 화면으로 이동
        if (!StringUtils.hasText(token)) {
            return "user/reset_password_verification";
        }

        //유효하지 않은 토큰이면 예외 발생
        if (!jwtGenerator.isValid(token, TokenType.RESET_PASSWORD)) {
            throw new JwtException("유효한 토큰이 아닙니다.");
        }

        //시간 연장을 위해 토큰을 새로 발급
        ResetPasswordTokenParseData resetPasswordTokenParseData = jwtGenerator.parseResetPasswordToken(token);
        token = jwtGenerator.generateResetPasswordToken(resetPasswordTokenParseData.getUserId());
        model.addAttribute("token", token);
        return "user/reset_password_form";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @Valid PasswordParam passwordParam ,
                                HttpServletResponse response) {
        userService.resetPassword(token, passwordParam.getPassword());
        Cookie cookie = CookieUtils.createMessageCookie("비밀번호 변경 완료", "/reset-password/complete");
        response.addCookie(cookie);
        return "redirect:/reset-password/complete";
    }

    @GetMapping("/reset-password/complete")
    public String resetPasswordComplete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String message = CookieUtils.resolveMessageCookie(request);
        if (!StringUtils.hasText(message)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        CookieUtils.removeMessageCookie(response, "/reset-password/complete");
        return "user/reset_password_complete";
    }

    @ExceptionHandler(value = {UserAlreadyRegisteredException.class, JwtException.class})
    public String joinProcessHandler(Exception e, HttpServletResponse response) {

        log.info("회원 가입 진행 중 예외 발생", e);
        String message = "";
        if (e instanceof UserAlreadyRegisteredException) {
            message = "이미 가입이 완료된 상태입니다.";
        } else if (e instanceof JwtException) {
            message = "존재하지 않거나 시간이 지나 만료된 페이지입니다.";
        }
        response.addCookie(CookieUtils.createMessageCookie(message));
        return "redirect:/";
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public String handlerUserNotFoundEx(UserNotFoundException e, HttpServletResponse response) {
        log.info("유저를 찾을 수 없음", e);
        String message = "존재하지 않는 사용자입니다.";
        response.addCookie(CookieUtils.createMessageCookie(message));
        return "redirect:/";
    }
}
