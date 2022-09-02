package com.bluesky.mainservice.unit.validation;

import com.bluesky.mainservice.controller.community.board.constant.BoardOption;
import com.bluesky.mainservice.controller.community.board.validation.BoardTitleValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

@ExtendWith(MockitoExtension.class)
public class BoardTitleValidatorTest {

    BoardTitleValidator boardTitleValidator = new BoardTitleValidator();

    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Test
    @DisplayName("공백을 제외한 길이가 0 이면 false")
    void if_zero_length_without_whitespace_return_false() {
        //given
        String value = "";

        //when
        boolean result = boardTitleValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @DisplayName("최대 길이 보다 크면 false")
    @Test
    void if_gt_max_length_return_false() {
        //given
        String value = "A".repeat(BoardOption.MAX_TITLE_LENGTH + 1);

        //when
        boolean result = boardTitleValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @DisplayName("탭이 포함되었으면 false")
    @Test
    void if_contains_tab_return_false() {
        //given
        String value = "hello\tworld";

        //when
        boolean result = boardTitleValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();

    }

    @DisplayName("개행이 포함되었으면 false")
    @Test
    void if_contains_new_line_return_false() {
        //given
        String value = "hello\nworld";

        //when
        boolean result = boardTitleValidator.isValid(value, constraintValidatorContext);

        //then
        Assertions.assertThat(result).isFalse();
    }
}
