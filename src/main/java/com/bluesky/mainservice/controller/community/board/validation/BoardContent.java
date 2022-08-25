package com.bluesky.mainservice.controller.community.board.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = BoardContentValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface BoardContent {

    String message() default "게시글 내용이 유효하지 않음";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
