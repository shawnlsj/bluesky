package com.bluesky.mainservice.controller.user.validation;

import com.bluesky.mainservice.util.RegexUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.bluesky.mainservice.controller.user.constant.UserOption.*;

public class NicknameValidator implements ConstraintValidator<Nickname, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //제한 길이를 벗어났으면 false
        //탭, 개행, 공백 문자가 들어있으면 false
        return value.length() >= MIN_NICKNAME_LENGTH
                && value.length() <= MAX_NICKNAME_LENGTH
                && !RegexUtils.contains(value, "\\t|\\R|\\s");
    }
}
