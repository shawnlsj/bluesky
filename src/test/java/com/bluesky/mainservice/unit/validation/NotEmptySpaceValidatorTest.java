package com.bluesky.mainservice.unit.validation;

import com.bluesky.mainservice.controller.validation.NotEmptySpace;
import com.bluesky.mainservice.controller.validation.NotEmptySpaceValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class NotEmptySpaceValidatorTest {

    NotEmptySpaceValidator notEmptySpaceValidator = new NotEmptySpaceValidator();

    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Mock
    NotEmptySpace constraintAnnotation;

    @Test
    @DisplayName("탭 문자가 들어있으면 false")
    void if_contains_tab_return_false() {
        //given
        given(constraintAnnotation.allowWhitespace()).willReturn(false);
        notEmptySpaceValidator.initialize(constraintAnnotation);

        //when
        String value = "hello\tworld";
        boolean result = notEmptySpaceValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    @DisplayName("개행 문자가 들어있으면 false")
    void if_contains_new_line_return_false() {
        //given
        given(constraintAnnotation.allowWhitespace()).willReturn(false);
        notEmptySpaceValidator.initialize(constraintAnnotation);

        //when
        String value = "hello\nworld";
        boolean result = notEmptySpaceValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    @DisplayName("allowWhiteSpace 가 true 이면 공백 허용")
    void allow_whitespace_when_allowWhiteSpace_is_true() {
        //given
        given(constraintAnnotation.allowWhitespace()).willReturn(true);
        notEmptySpaceValidator.initialize(constraintAnnotation);

        //when
        String value = "hello world";
        boolean result = notEmptySpaceValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    @DisplayName("allowWhiteSpace 가 false 이면 공백 불가")
    void now_allow_whitespace_when_allowWhiteSpace_is_false() {
        //given
        given(constraintAnnotation.allowWhitespace()).willReturn(false);
        notEmptySpaceValidator.initialize(constraintAnnotation);

        //when
        String value = "hello world";
        boolean result = notEmptySpaceValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }
}
