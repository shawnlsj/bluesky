package com.bluesky.mainservice.controller.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = TextSizeValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface TextSize {
    int min() default 1;
    int max();
    String message() default "정해진 범위를 벗어났음";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
