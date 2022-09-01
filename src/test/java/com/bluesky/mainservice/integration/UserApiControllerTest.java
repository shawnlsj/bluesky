package com.bluesky.mainservice.integration;

import com.bluesky.mainservice.repository.user.UserRepository;
import com.bluesky.mainservice.repository.user.constant.AccountType;
import com.bluesky.mainservice.repository.user.domain.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
public class UserApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("닉네임 사용 가능성 테스트")
    class test_check_nickname_availability {

        @Order(1)
        @Test
        @DisplayName("사용 가능한 닉네임이면 true")
        void if_available_nickname_expected_true() throws Exception {
            //given
            String nickname = "tester";

            //when
            ResultActions result = mvc.perform(get("/user/availability")
                    .param("nickname", nickname)
                    .contentType(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.available").value(true));
        }

        @Order(2)
        @Test
        @DisplayName("사용할 수 없는 닉네임이면 false")
        void if_not_available_nickname_expected_false() throws Exception {
            //given
            String nickname = "tester";
            User user = User.builder()
                    .email("test@test.co.kr")
                    .password("1234")
                    .accountType(AccountType.ORIGINAL)
                    .nickname(nickname)
                    .build();
            userRepository.save(user);

            //when
            ResultActions result = mvc.perform(get("/user/availability")
                    .param("nickname", nickname)
                    .contentType(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.available").value(false));
        }
    }
}
