package com.bluesky.mainservice.controller.community.board.validation;

import com.bluesky.mainservice.util.RegexUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.bluesky.mainservice.controller.community.board.constant.BoardOption.*;

public class BoardTitleValidator implements ConstraintValidator<BoardTitle, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //공백 외의 문자가 1개 이상 들어있지 않으면 false
        //제한길이를 초과하였으면 false
        if (!StringUtils.hasText(value) || value.length() > MAX_TITLE_LENGTH) {
            return false;
        }

        //허용되지 않은 문자가 포함되어 있으면 false
        return !RegexUtils.contains(value, "(\\t|\\R)");
    }
}
