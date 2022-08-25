package com.bluesky.mainservice.controller.community.board.validation;

import com.bluesky.mainservice.util.RegexUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.bluesky.mainservice.controller.community.board.constant.ReplyOption.MAX_CONTENT_LENGTH;

public class ReplyContentValidator implements ConstraintValidator<ReplyContent, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        
        //공백 외의 문자가 1개 이상 들어있지 않으면 false
        if (!StringUtils.hasText(value)) {
            return false;
        }

        //줄바꿈 태그를 없애고, 이스케이프된 문자를 길이가 1인 문자로 치환
        int textContentLength = value
                .replace("<br>", "")
                .replaceAll("(&amp;|&lt;|&gt;)", " ")
                .length();

        //제한길이를 초과하였으면 false
        if (textContentLength > MAX_CONTENT_LENGTH) {
            return false;
        }

        //허용되지 않은 문자가 포함되어 있으면 false
        String noTagNoEscapedCharText = value.replaceAll("(<br>|&amp;|&lt;|&gt;)", "");
        return !RegexUtils.contains(noTagNoEscapedCharText, "(\\t|\\R|<|>|&)");
    }
}
