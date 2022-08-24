package com.bluesky.mainservice.controller.community.board.validation;

import com.bluesky.mainservice.util.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.bluesky.mainservice.controller.community.board.constant.BoardOption.MAX_CONTENT_LENGTH;
import static com.bluesky.mainservice.controller.community.board.constant.BoardOption.MAX_HTML_LENGTH;

@Slf4j
public class BoardContentValidator implements ConstraintValidator<BoardContent, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //공백 외의 문자가 1개 이상 들어있지 않으면 false
        if (!StringUtils.hasText(value)) {
            return false;
        }

        //html 길이 제한을 초과하였으면 false
        if (value.length() > MAX_HTML_LENGTH) {
            log.debug("htmlLength : {}", value.length());
            return false;
        }

        //길이 유효성 검사를 위해 태그를 지우고,
        //이스케이프된 문자를 원래대로 치환
        int textContentLength = value
                .replaceAll("<.+?>", "")
                .replaceAll("(&amp;|&lt;|&gt;)", " ")
                .length();

        log.debug("textContentLength : {}", textContentLength);

        //텍스트 길이가 0 이거나 제한길이를 초과하였으면 false
        if (textContentLength == 0 || textContentLength > MAX_CONTENT_LENGTH) {
            return false;
        }

        //태그와 이스케이프 대상 문자를 제거 후 허용하지 않은 문자가 들어있는지 확인
        String noTagNoEscapedCharText = value.replaceAll("(<.+?>|&amp;|&lt;|&gt;)", "");
        boolean containNotAllowedChar = RegexUtils.contains(noTagNoEscapedCharText, "(\\t|\\R|&)");
        return !containNotAllowedChar;
    }
}
