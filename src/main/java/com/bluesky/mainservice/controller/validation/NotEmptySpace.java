package com.bluesky.mainservice.controller.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = NotEmptySpaceValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface NotEmptySpace {

    boolean allowWhiteSpace() default false;
    String message() default "문자열에 공백이 있음";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
