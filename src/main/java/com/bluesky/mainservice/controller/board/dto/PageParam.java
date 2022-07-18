package com.bluesky.mainservice.controller.board.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Positive;

import static com.bluesky.mainservice.controller.board.constant.BoardOption.*;

@Getter
@Setter
public class PageParam {

    @Positive
    private int page = 1;

    public int calculateStartPageNum() {
        //요청한 페이지 번호를 페이지네비게이션 크기로 나눈 몫을 이용하여 계산식을 만들 때,
        //나머지가 없는 경우는 페이지네비게이션 내의 다른 번호보다 몫이 1이 더 많으므로 분기를 나눈다
        if (page % PAGE_NAVIGATION_SIZE == 0) {
            //예) 요청한 페이지 번호가 90번, 페이지네비게이션 크기가 10이라고 하면
            //시작 번호는 81번이 되야 하므로 (몫 - 1) * 10 을 한 뒤 1을 더해준다
            return ((page / PAGE_NAVIGATION_SIZE) - 1) * PAGE_NAVIGATION_SIZE + 1;
        } else {
            //예) 요청한 페이지 번호가 88번, 페이지 네이션 크기가 10이라고 하면
            //시작 번호는 81번이 되야 하므로 몫 * 10 을 한 뒤 1을 더해준다
            return (page / PAGE_NAVIGATION_SIZE) * PAGE_NAVIGATION_SIZE + 1;
        }
    }

    public int calculateEndPageNum(int totalPagesNum) {
        //요청한 페이지 번호를 페이지네비게이션 크기로 나눈 몫을 이용하여 계산식을 만들 때,
        //나머지가 없는 경우는 페이지네비게이션 내의 다른 번호보다 몫이 1이 더 많으므로 분기를 나눈다
        if (page % PAGE_NAVIGATION_SIZE == 0) {
            //요청한 페이지 번호를 페이지네비게이션 크기로 나눈 나머지가 0이라는 것은
            //요청한 페이지 번호가 페이지네이션의 끝 번호라는 것을 의마한다
            return page;
        } else {
            //예) 요청한 페이지 번호가 87, 페이지네비게이션 크기가 10이라고 하면
            //마지막 번호는 90이 되야 하므로 (몫 + 1) * 10 을 한다
            int endOfPageNavigationNum = ((page / PAGE_NAVIGATION_SIZE) + 1) * PAGE_NAVIGATION_SIZE;

            //총 페이지 수가 네비게이션의 마지막 번호보다 작은 경우는
            //총 페이지 수가 네비게이션의 마지막 번호가 된다
            return Math.min(totalPagesNum, endOfPageNavigationNum);
        }
    }
}
