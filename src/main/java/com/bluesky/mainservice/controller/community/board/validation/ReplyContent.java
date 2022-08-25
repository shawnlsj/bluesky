package com.bluesky.mainservice.controller.community.board.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ReplyContentValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface ReplyContent {

    String message() default "댓글 내용이 유효하지 않음";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
