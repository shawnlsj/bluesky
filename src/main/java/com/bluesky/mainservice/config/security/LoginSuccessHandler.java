package com.bluesky.mainservice.config.security;

import com.bluesky.mainservice.config.security.jwt.JwtMapper;
import com.bluesky.mainservice.config.security.jwt.JwtGenerator;
import com.bluesky.mainservice.repository.user.domain.User;
import com.bluesky.mainservice.service.user.LoginService;
import com.bluesky.mainservice.service.user.dto.LoginTokenSet;
import com.bluesky.mainservice.service.user.security.OAuth2UserDetails;
import com.bluesky.mainservice.service.user.security.OriginalAuthUserDetails;
import com.bluesky.mainservice.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtGenerator jwtGenerator;
    private final LoginService loginService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        //쿠키에서 리다이렉트 주소를 획득
        String redirectLocation = CookieUtils.resolveRedirectCookie(request);

        //다음 조건 중 하나에 해당하면 리다이렉트 주소를 루트로 설정
        //1. 리다이렉트 주소를 가지고 있지 않음
        //2. 리다이렉트 주소의 프로토콜이 https 가 아님
        //3. 리다이렉트 주소의 도메인이 요청 url 의 도메인과 일치하지 않음
        if (!StringUtils.hasText(redirectLocation) ||
                !redirectLocation.startsWith("https://" + request.getServerName())) {
            redirectLocation = "/";
        }

        //인증 정보를 획득
        Object principal = authentication.getPrincipal();

        //이메일 로그인인지 소셜 로그인인지 확인한다
        //이메일 로그인인 경우 액세스, 리프레시 토큰을 발급한다
        LoginTokenSet loginTokenSet;
        if (principal instanceof OriginalAuthUserDetails) {
            OriginalAuthUserDetails userInfo = (OriginalAuthUserDetails) principal;

            loginTokenSet = loginService.issueLoginTokenSet(userInfo.getUser());
            JwtMapper.addAccessToken(response, loginTokenSet.getAccessToken());
            JwtMapper.addRefreshToken(response, loginTokenSet.getRefreshToken());

            redirectLocation = CookieUtils.resolveRedirectCookie(request);

            //소셜 로그인인 경우 가입 완료된 사용자에 한해서만 액세스 토큰과 리프레시 토큰을 발근한다
        } else if (principal instanceof OAuth2UserDetails) {
            OAuth2UserDetails userInfo = (OAuth2UserDetails) principal;
            User user = userInfo.getUser();

            //신규 회원일 경우 추가 정보 입력 창으로 리다이렉트
            if (!user.isRegisteredUser()) {
                String token = jwtGenerator.generateJoinToken(user.getSocialLoginId(), user.getAccountType());
                redirectLocation = UriComponentsBuilder
                        .fromUriString("/join")
                        .queryParam("token", token)
                        .build().toUriString();
            } else {
                //신규 회원이 아닐 경우 액세스, 리프레시 토큰을 발급한다
                loginTokenSet = loginService.issueLoginTokenSet(userInfo.getUser());
                JwtMapper.addAccessToken(response, loginTokenSet.getAccessToken());
                JwtMapper.addRefreshToken(response, loginTokenSet.getRefreshToken());
            }
        }
        response.sendRedirect(redirectLocation);
    }
}
