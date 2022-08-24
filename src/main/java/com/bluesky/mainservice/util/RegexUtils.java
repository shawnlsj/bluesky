package com.bluesky.mainservice.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    //[로컬파트]
    //^[^@]+@ -> @ 앞에 @을 제외한 문자가 1개 이상 있다.
    //[도메인]
    //[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])* -> (도메인 공통 사항) 도메인은 처음과 끝에 하이픈이 오지 않는다.
    //(\\.[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])*)* -> TLD 밑의 도메인이 없거나 1개 이상 있다.
    //(\\.[a-zA-Z][a-zA-Z0-9-]*[a-zA-Z0-9])$ -> TLD 는 또한 문자로 시작하며 길이가 최소 2글자 이상이다.
    public static final String EMAIL_VALIDATION_REGEX = "^[^@]+@[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])*(\\.[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])*)*(\\.[a-zA-Z][a-zA-Z0-9-]*[a-zA-Z0-9])$";

    //첫 글자, @ 뒤의 첫 글자, 최상위 도메인을 제외하고 마스킹한다.
    //foo@bar.co.kr -> f**@b*****.kr
    //[캡처링 조건]
    //1. (?<=[^@]) -> @을 제외한 문자가 앞에 있다.
    //2. [^@] -> @을 제외한 문자이다.
    //3. (?=.*\\.) -> 0개 이상의 문자열 + 점(.) 으로 구성된 부분이 뒤에 있다.
    public static final String MASKING_EMAIL_REGEX = "(?<=[^@])[^@](?=.*\\.)";

    public static String maskingEmail(String email) {
        return email.replaceAll(MASKING_EMAIL_REGEX, "*");
    }

    public static boolean isContainNumber(String str) {
        Pattern pattern = Pattern.compile("(?=[0-9]).");
        return pattern.matcher(str).find();
    }

    public static boolean isContainAlphabet(String str) {
        Pattern pattern = Pattern.compile("(?=[a-zA-Z]).");
        return pattern.matcher(str).find();
    }

    //매칭되는 특수문자 !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
    public static boolean isContainSpecialChar(String str) {
        Pattern pattern = Pattern.compile("(?=[!\"#$%&'()*+,\\-./:;<=>?@\\[\\\\\\]^_`{|}~]).+");
        return pattern.matcher(str).find();
    }
    
    public static boolean contains(String str, String regex){
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(str);
        return matcher.find();
    }
}
