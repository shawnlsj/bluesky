package com.bluesky.mainservice.unit.validation;

import com.bluesky.mainservice.controller.user.constant.UserOption;
import com.bluesky.mainservice.controller.user.validation.NicknameValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

@ExtendWith(MockitoExtension.class)
public class NicknameValidatorTest {

    NicknameValidator nicknameValidator = new NicknameValidator();

    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @DisplayName("공백이 포함되었으면 false")
    @Test
    void if_contains_whitespace_return_false() {
        //given
        String value = "hello world";

        //when
        boolean result = nicknameValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @DisplayName("탭이 포함되었으면 false")
    @Test
    void if_contains_tab_return_false() {
        //given
        String value = "hello\tworld";

        //when
        boolean result = nicknameValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();

    }

    @DisplayName("개행이 포함되었으면 false")
    @Test
    void if_contains_new_line_return_false() {
        //given
        String value = "hello\nworld";

        //when
        boolean result = nicknameValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @DisplayName("최대 길이 보다 크면 false")
    @Test
    void if_gt_max_length_return_false() {
        //given
        String value = "A".repeat(UserOption.MAX_NICKNAME_LENGTH + 1);

        //when
        boolean result = nicknameValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @DisplayName("최소 길이 보다 작으면 false")
    @Test
    void if_gt_min_length_return_false() {
        //given
        String value = "A".repeat(UserOption.MIN_NICKNAME_LENGTH - 1);

        //when
        boolean result = nicknameValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }
}
