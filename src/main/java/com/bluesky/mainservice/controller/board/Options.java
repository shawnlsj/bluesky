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

    //페이지 네비게이션에 표시될 페이지의 수
    static final int PAGINATION_SIZE = 10;
}
