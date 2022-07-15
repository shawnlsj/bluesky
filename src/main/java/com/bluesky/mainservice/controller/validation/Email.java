package com.bluesky.mainservice.controller.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = EmailValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface Email {
    String message() default "이메일 형식이 올바르지 않음";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
