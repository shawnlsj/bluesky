package com.bluesky.mainservice.unit.validation;

import com.bluesky.mainservice.controller.community.board.constant.ReplyOption;
import com.bluesky.mainservice.controller.community.board.validation.ReplyContentValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class ReplyContentValidatorTest {

    ReplyContentValidator replyContentValidator = new ReplyContentValidator();

    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Test
    @DisplayName("공백을 제외한 길이가 0 이면 false")
    void if_zero_length_without_whitespace_return_false() {
        //given
        String value = "";

        //when
        boolean result = replyContentValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @DisplayName("최대 길이 보다 크면 false")
    @Test
    void if_gt_max_length_return_false() {
        //given
        String ampersand = "&amp;";
        String value = ampersand.repeat(ReplyOption.MAX_CONTENT_LENGTH + 1);

        //when
        boolean result = replyContentValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("허용되지 않은 문자가 포함되어 있으면 false")
    void if_contains_not_allowed_char_return_false(String value) {
        //when
        boolean result = replyContentValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }

    static List<String> if_contains_not_allowed_char_return_false() {
        List<String> nowAllowedCharList = new ArrayList<>();
        nowAllowedCharList.add("new line \n\r");
        nowAllowedCharList.add("tab \t");
        nowAllowedCharList.add("<");
        nowAllowedCharList.add(">");
        nowAllowedCharList.add("&");
        return nowAllowedCharList;
    }
}
