package com.bluesky.mainservice.controller.validation;

import com.bluesky.mainservice.util.RegexUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotEmptySpaceValidator implements ConstraintValidator<NotEmptySpace, String> {

    boolean allowWhiteSpace;

    @Override
    public void initialize(NotEmptySpace constraintAnnotation) {
        allowWhiteSpace = constraintAnnotation.allowWhiteSpace();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (allowWhiteSpace) {
            return !RegexUtils.contains(value, "\\R|\\t");
        }
        return !RegexUtils.contains(value, "\\s|\\R|\\t");
    }
}
