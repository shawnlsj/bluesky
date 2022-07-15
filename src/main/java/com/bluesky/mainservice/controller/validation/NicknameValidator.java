package com.bluesky.mainservice.controller.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.bluesky.mainservice.controller.user.constant.UserOption.*;

public class NicknameValidator implements ConstraintValidator<Nickname, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value.length() < MIN_NICKNAME_LENGTH
                || value.length() > MAX_NICKNAME_LENGTH
                || value.matches("\\s")) {
            return false;
        }
        return true;
    }
}
