package com.bluesky.mainservice.controller.user;

import com.bluesky.mainservice.config.security.jwt.JwtMapper;
import com.bluesky.mainservice.config.security.jwt.JwtProvider;
import com.bluesky.mainservice.controller.user.dto.JoinForm;
import com.bluesky.mainservice.controller.user.dto.OAuth2JoinForm;
import com.bluesky.mainservice.repository.user.constant.AccountType;
import com.bluesky.mainservice.service.user.UserService;
import com.bluesky.mainservice.service.user.dto.LoginTokenSet;
import com.bluesky.mainservice.service.user.dto.UserAccountInfo;
import com.bluesky.mainservice.service.user.dto.UserRegisterData;
import com.bluesky.mainservice.service.user.exception.UserAlreadyRegisteredException;
import com.bluesky.mainservice.service.user.exception.UserNotFoundException;
import com.bluesky.mainservice.util.CookieUtils;
import com.bluesky.mainservice.util.RegexUtils;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.bluesky.mainservice.controller.user.dto.UserResponseDto.JoinUserInfo;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

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

        //유저 계정 정보를 가져온다
        UserAccountInfo userAccountInfo = userService.createUserAccountInfo(token);

        //이메일 가입 유저인 경우 시간 연장을 위해 토큰을 다시 발급
        boolean isOriginalUser = (userAccountInfo.getAccountType() == AccountType.ORIGINAL);
        if (isOriginalUser) {
            token = jwtProvider.generateJoinToken(userAccountInfo.getEmail(), AccountType.ORIGINAL);
        }


        //화면에서 사용할 dto 생성
        String maskedEmail = RegexUtils.maskingEmail(userAccountInfo.getEmail());
        JoinUserInfo joinUserInfo = new JoinUserInfo(token, maskedEmail, isOriginalUser);
        model.addAttribute("joinUserInfo", joinUserInfo);
        model.addAttribute("userSaveForm", new JoinForm());
        return "user/join_form";
    }

    @GetMapping("/join/complete")
    public String complete(HttpServletRequest request, HttpServletResponse response, Model model) {
        //쿠키에서 가입 완료된 사용자의 닉네임을 획득
        String nickname = CookieUtils.resolveMessageCookie(request);

        //저장되어 있는 닉네임이 없는 경우 404 예외를 던짐
        if (!StringUtils.hasText(nickname)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        model.addAttribute("nickname", nickname);

        CookieUtils.removeMessageCookie(response, "/join/complete");
        return "user/join_complete";
    }

    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm, @RequestParam String token, HttpServletResponse response) {
        //서비스에 넘겨줄 dto 생성
        UserRegisterData data = UserRegisterData.builder()
                .nickname(joinForm.getNickname())
                .password(joinForm.getPassword())
                .isOriginalUser(true)
                .build();

        //유저를 등록하고, 발급받은 로그인용 토큰을 응답에 추가
        LoginTokenSet loginTokenSet = userService.register(data, token);
        JwtMapper.addAccessToken(response, loginTokenSet.getAccessToken());
        JwtMapper.addRefreshToken(response, loginTokenSet.getRefreshToken());

        //가입 완료된 닉네임을 쿠키에 저장
        response.addCookie(CookieUtils.createMessageCookie(joinForm.getNickname(), "/join/complete"));
        return "redirect:/join/complete";
    }

    @PostMapping("/join/oauth2")
    public String join(@Valid OAuth2JoinForm joinForm, @RequestParam String token, HttpServletResponse response) {
        //서비스에 넘겨줄 dto 생성
        UserRegisterData data = UserRegisterData.builder()
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

    @ExceptionHandler(value = {UserAlreadyRegisteredException.class, UserNotFoundException.class, JwtException.class})
    public String joinProcessHandler(Exception e, HttpServletResponse response) {
        String message = "";
        if (e instanceof UserAlreadyRegisteredException) {
            message = "이미 가입이 완료된 상태입니다.";
        } else if (e instanceof UserNotFoundException) {
            message = "존재하지 않는 사용자입니다.";
        } else if (e instanceof JwtException) {
            message = "존재하지 않거나 시간이 지나 만료된 페이지입니다.";
        }
        response.addCookie(CookieUtils.createMessageCookie(message));
        return "redirect:/";
    }

    @ExceptionHandler(value = DataAccessException.class)
    public String handleDataAccessEx(HttpServletResponse response) {
        String message = "서버에 일시적인 오류가 발생하였습니다.";
        response.addCookie(CookieUtils.createMessageCookie(message));
        return "redirect:/";
    }
}
