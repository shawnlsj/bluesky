package com.bluesky.mainservice.controller.validation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;

@Slf4j
public class TextSizeValidator implements ConstraintValidator<TextSize, String> {
    private int min;
    private int max;

    @Override
    public void initialize(TextSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        int size = value.replaceAll("<.+?>", "").replaceAll("&nbsp;", " ").length();
        log.debug("size : {}", size);
        if (size >= min && size <= max) {
            return true;
        }
        return false;
    }
}
