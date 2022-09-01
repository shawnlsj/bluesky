package com.bluesky.mainservice.unit.validation;

import com.bluesky.mainservice.controller.user.constant.UserOption;
import com.bluesky.mainservice.controller.user.validation.EmailValidator;
import com.bluesky.mainservice.util.RegexUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("이메일 테스트")
public class EmailTest {

    @Test
    @DisplayName("이메일 마스킹 테스트")
    void masking_email_test() {
        //given
        String email = "hello2022@mycompany.co.kr";

        //when
        String result = RegexUtils.maskingEmail(email);

        //then
        String expected = new StringBuilder()
                .append("h")
                .append("ello2022".replaceAll(".", "*"))
                .append("@m")
                .append("ycompnay.co".replaceAll(".", "*"))
                .append(".kr")
                .toString();
        assertThat(result).isEqualTo(expected);
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("이메일 유효성 검사 테스트")
    class EmailValidatorTest {
        EmailValidator emailValidator = new EmailValidator();

        @Mock
        ConstraintValidatorContext constraintValidatorContext;

        @Order(1)
        @ParameterizedTest
        @MethodSource
        @DisplayName("올바른 이메일 형식이면 true")
        void when_correct_format_expect_true(String email) {
            //when
            boolean result = emailValidator.isValid(email, constraintValidatorContext);

            //then
            assertThat(result).isTrue();
        }

        Stream<Arguments> when_correct_format_expect_true() {
            return Stream.of(
                    arguments("blue@sky.co.kr"),
                    arguments("blue@sky.co.co.kr"),
                    arguments("blue@s.k.y.com"),
                    arguments("blue@sky.kor"),
                    arguments("blue@sky12.co12.kr12"),
                    arguments("blue@12sky.12co.kr12"),
                    arguments("1234blue1234@s.kr"));
        }

        @Order(2)
        @ParameterizedTest
        @MethodSource
        @DisplayName("잘못된 이메일 형식이면 false")
        void when_wrong_format_expect_false(String email) {
            //when
            boolean result = emailValidator.isValid(email, constraintValidatorContext);

            //then
            assertThat(result).isFalse();
        }

        Stream<Arguments> when_wrong_format_expect_false() {
            return Stream.of(
                    arguments("blue@sky.co.kr-"),
                    arguments("blue@sky.co.-kr"),
                    arguments("blue@sky.co.1kr"),
                    arguments("blue@-s.co.kr"),
                    arguments("blue@s-.co.kr"),
                    arguments("blue@s.k.y"),
                    arguments("blue@.com"),
                    arguments("blue@com."),
                    arguments("blue@com-"),
                    arguments("blue@com"));
        }

        @Order(3)
        @Test
        @DisplayName("제한 길이를 넘어가면 false")
        void when_over_max_length_expect_false() {
            //given
            StringBuilder sb = new StringBuilder();
            sb.append("this_is_over_length_email_");
            sb.append("A".repeat(UserOption.MAX_EMAIL_LENGTH));
            sb.append("@sky.co.kr");
            String email = sb.toString();

            //when
            boolean result = emailValidator.isValid(email, constraintValidatorContext);

            //then
            assertThat(result).isFalse();
        }
    }
}
