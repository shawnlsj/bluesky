package com.bluesky.mainservice.unit;

import com.bluesky.mainservice.controller.user.constant.UserOption;
import com.bluesky.mainservice.controller.user.validation.PasswordValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.Mock;

import javax.validation.ConstraintValidatorContext;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PasswordValidatorTest {
    PasswordValidator passwordValidator = new PasswordValidator();

    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Order(1)
    @Test
    @DisplayName("최소 길이보다 작을 경우 false")
    void when_less_than_min_length_expect_false() {
        //given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < UserOption.MIN_PASSWORD_LENGTH - 1; i++) {
            if (i == 0) {
                sb.append("a");
            } else {
                sb.append((i * 10) % 7);
            }
        }
        String password = sb.toString();

        //when
        boolean result = passwordValidator.isValid(password, constraintValidatorContext);

        //than
        Assertions.assertThat(result).isFalse();
    }

    @Order(2)
    @Test
    @DisplayName("최대 길이보다 클 경우 false")
    void when_more_than_max_length_expect_false() {
        //given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < UserOption.MAX_PASSWORD_LENGTH + 1; i++) {
            if (i == 0) {
                sb.append("a");
            } else {
                sb.append((i * 10) % 7);
            }
        }
        String password = sb.toString();

        //when
        boolean result = passwordValidator.isValid(password, constraintValidatorContext);

        //than
        Assertions.assertThat(result).isFalse();
    }

    @Order(3)
    @Test
    @DisplayName("영문만 포함된 경우 false")
    void when_contains_only_alphabet_expect_false() {
        //given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < UserOption.MIN_PASSWORD_LENGTH ; i++) {
            sb.append((char) (97 + (i % 7)));
        }
        String password = sb.toString();

        //when
        boolean result = passwordValidator.isValid(password, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Order(4)
    @Test
    @DisplayName("숫자만 포함된 경우 false")
    void when_contains_only_number_expect_false() {
        //given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < UserOption.MIN_PASSWORD_LENGTH; i++) {
            sb.append((i * 10) % 7);
        }
        String password = sb.toString();

        //when
        boolean result = passwordValidator.isValid(password, constraintValidatorContext);

        //than
        Assertions.assertThat(result).isFalse();
    }

    @Order(5)
    @Test
    @DisplayName("특수문자만 포함된 경우 false")
    void when_contains_only_special_char_expect_false() {
        //given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < UserOption.MIN_PASSWORD_LENGTH ; i++) {
            sb.append((char) (41 + (i % 7)));
        }
        String password = sb.toString();

        //when
        boolean result = passwordValidator.isValid(password, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Order(6)
    @Test
    @DisplayName("공백이 포함된 경우 false")
    void when_contains_space_expect_false() {
        //given
        char space = 32;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < UserOption.MIN_PASSWORD_LENGTH; i++) {
            if (i % 4 == 0) {
                sb.append(space);
            } else if (i % 4 == 1) {
                sb.append((char) (97 + (i % 7)));
            } else if (i % 4 == 2) {
                sb.append((char) (41 + (i % 7)));
            } else {
                sb.append((i * 10) % 7);
            }
        }
        String password = sb.toString();

        //when
        boolean result = passwordValidator.isValid(password, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("2종류 이상 포함 경우 true")
    class When_contains_more_than_two_types_expect_true {

        @Order(1)
        @Test
        @DisplayName("영문과 숫자만 포함된 경우")
        void when_contains_alphabet_and_number() {
            //given
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < UserOption.MIN_PASSWORD_LENGTH; i++) {
                if (i % 2 == 0) {
                    sb.append((i * 10) % 7);
                } else {
                    sb.append((char) (97 + (i % 7)));
                }
            }
            String password = sb.toString();

            //when
            boolean result = passwordValidator.isValid(password, constraintValidatorContext);

            //then
            Assertions.assertThat(result).isTrue();
        }

        @Order(2)
        @Test
        @DisplayName("영문과 특수문자만 포함된 경우")
        void when_contains_alphabet_and_special_char() {
            //given
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < UserOption.MIN_PASSWORD_LENGTH; i++) {
                if (i % 2 == 0) {
                    sb.append((char) (41 + (i % 7)));
                } else {
                    sb.append((char) (97 + (i % 7)));
                }
            }
            String password = sb.toString();

            //when
            boolean result = passwordValidator.isValid(password, constraintValidatorContext);

            //then
            Assertions.assertThat(result).isTrue();
        }

        @Order(3)
        @Test
        @DisplayName("숫자와 특수문자만 포함된 경우")
        void when_contains_number_and_special_char() {
            //given
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < UserOption.MIN_PASSWORD_LENGTH; i++) {
                if (i % 2 == 0) {
                    sb.append((i * 10) % 7);
                } else {
                    sb.append((char) (41 + (i % 7)));
                }
            }
            String password = sb.toString();

            //when
            boolean result = passwordValidator.isValid(password, constraintValidatorContext);

            //then
            Assertions.assertThat(result).isTrue();
        }

        @Order(4)
        @Test
        @DisplayName("모든 종류가 포함된 경우")
        void when_contains_all_types() {
            //given
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < UserOption.MIN_PASSWORD_LENGTH; i++) {
                if (i % 3 == 0) {
                    sb.append((i * 10) % 7);
                } else if (i % 3 == 1) {
                    sb.append((char) (97 + (i % 7)));
                } else {
                    sb.append((char) (41 + (i % 7)));
                }
            }
            String password = sb.toString();

            //when
            boolean result = passwordValidator.isValid(password, constraintValidatorContext);

            //then
            Assertions.assertThat(result).isTrue();
        }
    }
}
