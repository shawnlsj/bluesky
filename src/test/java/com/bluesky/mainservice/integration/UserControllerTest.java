package com.bluesky.mainservice.integration;

import com.bluesky.mainservice.config.security.jwt.JwtGenerator;
import com.bluesky.mainservice.config.security.jwt.JwtMapper;
import com.bluesky.mainservice.repository.user.UserRepository;
import com.bluesky.mainservice.repository.user.constant.AccountType;
import com.bluesky.mainservice.repository.user.domain.User;
import com.bluesky.mainservice.util.CookieUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("이메일 사용자 가입 테스트")
    void test_email_user_join() throws Exception {
        //given
        String nickname = "tester";
        String password = "abcd131@";
        String userId = "test@test.co.kr";
        String joinToken = jwtGenerator.generateJoinToken(userId, AccountType.ORIGINAL);

        //when
        ResultActions result = mvc.perform(post("/join")
                .param("nickname", nickname)
                .param("password", password)
                .param("token", joinToken));

        //then
        MockHttpServletResponse response = result.andReturn().getResponse();
        Cookie accessTokenCookie = response.getCookie(JwtMapper.ACCESS_TOKEN_COOKIE_NAME);
        Cookie refreshTokenCookie = response.getCookie(JwtMapper.REFRESH_TOKEN_COOKIE_NAME);
        Cookie messageCookie = response.getCookie(CookieUtils.MESSAGE_COOKIE_NAME);

        result.andExpect(status().is3xxRedirection());
        assertThat(accessTokenCookie).isNotNull();
        assertThat(refreshTokenCookie).isNotNull();
        assertThat(messageCookie).isNotNull();
        assertThat(messageCookie.getValue()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("소셜 미디어 사용자 가입 테스트")
    void test_oauth2_user_join() throws Exception {
        //given
        String userId = "1111";
        String email = "test@test.co.kr";
        AccountType accountType = AccountType.GOOGLE;
        String joinToken = jwtGenerator.generateJoinToken(userId, accountType);

        User user = User.builder()
                .email(email)
                .socialLoginId(userId)
                .accountType(accountType)
                .build();
        userRepository.save(user);

        //추가 입력 정보
        String nickname = "tester";

        //when
        ResultActions result = mvc.perform(post("/join/oauth2")
                .param("nickname", nickname)
                .param("token", joinToken));

        //then
        MockHttpServletResponse response = result.andReturn().getResponse();
        Cookie accessTokenCookie = response.getCookie(JwtMapper.ACCESS_TOKEN_COOKIE_NAME);
        Cookie refreshTokenCookie = response.getCookie(JwtMapper.REFRESH_TOKEN_COOKIE_NAME);
        Cookie messageCookie = response.getCookie(CookieUtils.MESSAGE_COOKIE_NAME);

        result.andExpect(status().is3xxRedirection());
        assertThat(accessTokenCookie).isNotNull();
        assertThat(refreshTokenCookie).isNotNull();
        assertThat(messageCookie).isNotNull();
        assertThat(messageCookie.getValue()).isEqualTo(nickname);
    }
}
