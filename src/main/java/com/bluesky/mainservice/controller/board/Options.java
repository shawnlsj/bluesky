package com.bluesky.mainservice.controller.board;

class Options {

    //제목 최대 글자 수
    static final int MAX_TITLE_SIZE = 50;

    //본문 최대 글자 수
    static final int MAX_CONTENT_SIZE = 300;

    //본문 엘리먼트의 innerHtml 프로퍼티가 가질 수 있는 최대 길이
    static final int MAX_HTML_SIZE = 1000;

    //1개의 페이지에 담기게 될 요소의 수
    static final int PAGE_SIZE = 10;

    //페이지네비게이션 크기: 값이 N이면 네비게이션 바에 페이지 이동 버튼이 N개 있음
    static final int PAGE_NAVIGATION_SIZE = 10;
}
