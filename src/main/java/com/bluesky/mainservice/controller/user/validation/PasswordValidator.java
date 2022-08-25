package com.bluesky.mainservice.controller.user.validation;

import com.bluesky.mainservice.util.RegexUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.bluesky.mainservice.controller.user.constant.UserOption.*;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //비밀번호의 길이가 올바른지 확인
        if (value.length() >= MIN_PASSWORD_LENGTH && value.length() <= MAX_PASSWORD_LENGTH) {

            //탭, 개행, 공백 문자가 들어있으면 false
            if (RegexUtils.contains(value, "\\t|\\R|\\s")) {
                return false;
            }

            //비밀번호에 영문/숫자/특수문자 중 몇 종류가 사용되었는지 카운트할 변수
            int matchCount = 0;

            //영문이 포함되어 있으면 카운트 +1
            if (RegexUtils.isContainAlphabet(value)) {
                matchCount++;
            }

            //숫자가 포함되어 있으면 카운트 +1
            if (RegexUtils.isContainNumber(value)) {
                matchCount++;
            }

            //특수문자가 포함되어 있으면 카운트 +1
            if (RegexUtils.isContainSpecialChar(value)) {
                matchCount++;
            }
            return matchCount >= 2;
        }
        return false;
    }
}
