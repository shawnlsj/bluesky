package com.bluesky.mainservice.controller.user.validation;

import com.bluesky.mainservice.util.RegexUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.bluesky.mainservice.controller.user.constant.UserOption.MAX_EMAIL_LENGTH;

public class EmailValidator implements ConstraintValidator<Email, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.matches(RegexUtils.EMAIL_VALIDATION_REGEX) && value.length() <= MAX_EMAIL_LENGTH;
    }
}
