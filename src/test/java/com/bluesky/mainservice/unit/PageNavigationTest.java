package com.bluesky.mainservice.unit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Slf4j
@DisplayName("페이지 네비게이션 테스트")
class PageNavigationTest {

    @ParameterizedTest(name = "{index}. 요청된 페이지: {0}, 네비게이션 크기: {1}, 예상 결과값: {2}")
    @MethodSource("calculateStartPageNumParams")
    @DisplayName("calculateStartPageNum 메소드 테스트")
    void calculateStartPageNumTest(int requestedPageNum, int pageNavigationSize, int exptected) {
        //when
        int result = calculateStartPageNum(requestedPageNum, pageNavigationSize);

        //then
        assertThat(result).isEqualTo(exptected);
    }

    static Stream<Arguments> calculateStartPageNumParams() {
        return Stream.of(
                arguments(1, 5, 1),
                arguments(3, 5, 1),
                arguments(5, 5, 1),
                arguments(6, 5, 6),
                arguments(8, 5, 6),
                arguments(10, 5, 6),
                arguments(11, 5, 11),
                arguments(1, 10, 1),
                arguments(5, 10, 1),
                arguments(10, 10, 1),
                arguments(11, 10, 11),
                arguments(15, 10, 11),
                arguments(20, 10, 11));
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("calculateEndPageNum 메소드 테스트")
    class CalculateEndPageNumTest {

        @ParameterizedTest(name = "{index}. 요청된 페이지: {0}, 네비게이션 크기: {1}, 총 페이지 수: {2}, 예상 결과값: {3}")
        @MethodSource
        @DisplayName("(총 페이지 수 >= 네비게이션의 끝 번호) 인 경우")
        void when_total_pages_ge_last_num_of_nav(int requestedPageNum, int pageNavigationSize, int totalPagesNum, int exptected) {
            //when
            int result = calculateEndPageNum(requestedPageNum, pageNavigationSize, totalPagesNum);

            //then
            assertThat(result).isEqualTo(exptected);
        }

        Stream<Arguments> when_total_pages_ge_last_num_of_nav() {
            return Stream.of(
                    arguments(1, 5, 100, 5),
                    arguments(5, 5, 100, 5),
                    arguments(6, 5, 100, 10),
                    arguments(1, 10, 100, 10),
                    arguments(10, 10, 100, 10),
                    arguments(11, 10, 100, 20),
                    arguments(20, 10, 100, 20));
        }

        @ParameterizedTest(name = "{index}. 요청된 페이지: {0}, 네비게이션 크기: {1}, 총 페이지 수: {2}, 예상 결과값: {3}")
        @MethodSource
        @DisplayName("(총 페이지 수 < 네비게이션의 끝 번호) 인 경우")
        void when_total_pages_lt_last_num_of_nav(int requestedPageNum, int pageNavigationSize, int totalPagesNum, int exptected) {
            //when
            int result = calculateEndPageNum(requestedPageNum, pageNavigationSize, totalPagesNum);

            //then
            assertThat(result).isEqualTo(exptected);
        }

        Stream<Arguments> when_total_pages_lt_last_num_of_nav() {
            return Stream.of(
                    arguments(1, 5, 1, 1),
                    arguments(2, 5, 4, 4),
                    arguments(6, 5, 6, 6),
                    arguments(1, 10, 7, 7),
                    arguments(5, 10, 9, 9),
                    arguments(11, 10, 15, 15),
                    arguments(22, 10, 22, 22));
        }
    }

    private int calculateStartPageNum(int requestedPageNum, int pageNavigationSize) {
        //요청한 페이지 번호를 페이지네비게이션 크기로 나눈 몫을 이용하여 계산식을 만들 때,
        //나머지가 없는 경우는 페이지네비게이션 내의 다른 번호보다 몫이 1이 더 많으므로 분기를 나눈다
        if (requestedPageNum % pageNavigationSize == 0) {
            //예) 요청한 페이지 번호가 90번, 페이지네비게이션 크기가 10이라고 하면
            //시작 번호는 81번이 되야 하므로 (몫 - 1) * 10 을 한 뒤 1을 더해준다
            return ((requestedPageNum / pageNavigationSize) - 1) * pageNavigationSize + 1;
        } else {
            //예) 요청한 페이지 번호가 88번, 페이지 네이션 크기가 10이라고 하면
            //시작 번호는 81번이 되야 하므로 몫 * 10 을 한 뒤 1을 더해준다
            return (requestedPageNum / pageNavigationSize) * pageNavigationSize + 1;
        }
    }

    private int calculateEndPageNum(int requestedPageNum, int pageNavigationSize, int totalPagesNum) {
        //요청한 페이지 번호를 페이지네비게이션 크기로 나눈 몫을 이용하여 계산식을 만들 때,
        //나머지가 없는 경우는 페이지네비게이션 내의 다른 번호보다 몫이 1이 더 많으므로 분기를 나눈다
        if (requestedPageNum % pageNavigationSize == 0) {
            //요청한 페이지 번호를 페이지네비게이션 크기로 나눈 나머지가 0이라는 것은
            //요청한 페이지 번호가 네비게이션의 끝 번호라는 것을 의마한다
            return requestedPageNum;
        } else {
            //예) 요청한 페이지 번호가 87, 페이지네비게이션 크기가 10이라고 하면
            //마지막 번호는 90이 되야 하므로 (몫 + 1) * 10 을 한다
            int endOfPageNavigationNum = ((requestedPageNum / pageNavigationSize) + 1) * pageNavigationSize;
            if (totalPagesNum >= endOfPageNavigationNum) {
                return endOfPageNavigationNum;
            } else {
                //총 페이지 수가 마지막 번호보다 작은 경우는 총 페이지 수가 마지막 번호가 된다
                return totalPagesNum;
            }
        }
    }
}
