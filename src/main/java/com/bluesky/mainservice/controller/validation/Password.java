package com.bluesky.mainservice.controller.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = PasswordValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface Password {
    String message() default "비밀번호 형식이 올바르지 않음";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
