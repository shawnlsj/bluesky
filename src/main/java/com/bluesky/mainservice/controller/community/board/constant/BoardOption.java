package com.bluesky.mainservice.controller.community.board.constant;

public class BoardOption {
    //제목 최대 글자 수
    public static final int MAX_TITLE_LENGTH = 50;

    //본문 최대 글자 수
    public static final int MAX_CONTENT_LENGTH = 200;

    //본문 엘리먼트의 innerHtml 프로퍼티가 가질 수 있는 최대 길이
    public static final int MAX_HTML_LENGTH = 1000;

    //게시글 목록 페이지 크기
    public static final int PAGE_SIZE_IN_DIRECTORY = 10;

    //페이지네비게이션 크기: 값이 N이면 네비게이션 바에 페이지 이동 버튼이 N개 있음
    public static final int PAGE_NAVIGATION_SIZE = 10;

    //일간 랭킹 게시글 페이지 크기
    public static final int DAILY_RANKING_PAGE_SIZE = 5;

    //홈 화면에서 조회할 게시글 크기
    public static final int PAGE_SIZE_IN_HOME = 13;
}
