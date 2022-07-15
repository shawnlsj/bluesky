package com.bluesky.mainservice.controller.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = NicknameValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface Nickname {
    String message() default "닉네임 형식이 올바르지 않음";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
