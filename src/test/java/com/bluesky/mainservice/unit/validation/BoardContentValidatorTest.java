package com.bluesky.mainservice.unit.validation;

import com.bluesky.mainservice.controller.community.board.constant.BoardOption;
import com.bluesky.mainservice.controller.community.board.validation.BoardContentValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

@ExtendWith(MockitoExtension.class)
public class BoardContentValidatorTest {

    BoardContentValidator boardContentValidator = new BoardContentValidator();

    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Test
    @DisplayName("공백을 제외한 길이가 0 이면 false")
    void if_zero_length_without_whitespace_return_false() {
        //given
        String value = "";

        //when
        boolean result = boardContentValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    @DisplayName("html 제한 길이를 초과하였으면 false")
    void if_gt_max_html_length_return_false() {
        //given
        String tag = "<br>";
        String value = tag.repeat((BoardOption.MAX_HTML_LENGTH / tag.length()) + 1);

        //when
        boolean result = boardContentValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    @DisplayName("텍스트 제한 길이를 초과하였으면 false")
    void if_gt_max_content_length_return_false() {
        //given
        String value = "a".repeat(BoardOption.MAX_CONTENT_LENGTH + 1);

        //when
        boolean result = boardContentValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    @DisplayName("허용되지 않은 이스케이프 문자가 있으면 false")
    void if_contains_not_allowed_escaped_char_return_false() {
        //given
        String value = "&nbsp;";

        //when
        boolean result = boardContentValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }
}
