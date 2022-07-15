package com.bluesky.mainservice.controller.validation;

import com.bluesky.mainservice.util.RegexUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.bluesky.mainservice.controller.user.constant.UserOption.MAX_EMAIL_LENGTH;

public class EmailValidator implements ConstraintValidator<Email, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value.matches(RegexUtils.EMAIL_VALIDATION_REGEX) && value.length() <= MAX_EMAIL_LENGTH) {
            return true;
        }
        return false;
    }
}
