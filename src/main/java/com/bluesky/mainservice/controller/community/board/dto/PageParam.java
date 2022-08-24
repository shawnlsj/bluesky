package com.bluesky.mainservice.controller.community.board.dto;

import lombok.Getter;

@Getter
public class PageParam {

    //요청에 page 파라미터가 없어 setter 가 호출되지 않더라도
    //page 값을 1로 반환할 수 있도록 기본값을 1로 초기화
    private int page = 1;

    public void setPage(int page) {
        if (page < 1) {
            return;
        }
        this.page = page;
    }
}
