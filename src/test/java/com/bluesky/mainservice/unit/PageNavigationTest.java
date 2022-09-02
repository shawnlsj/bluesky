package com.bluesky.mainservice.unit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.bluesky.mainservice.controller.community.board.dto.BoardResponseDto.PageNavigation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Slf4j
@DisplayName("페이지 네비게이션 테스트")
class PageNavigationTest {

    @MethodSource
    @DisplayName("calculateStartPageNum 메소드 테스트")
    @ParameterizedTest(name = "{index}. 요청된 페이지: {0}, 네비게이션 크기: {1}, 예상 결과값: {2}")
    void test_calculate_start_page_num(int requestedPageNum, int pageNavigationSize, int expected) {
        //given
        PageNavigation pageNavigation =
                new PageNavigation(requestedPageNum, Integer.MAX_VALUE, pageNavigationSize);

        //when
        int result = pageNavigation.getStartPageNum();

        //then
        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> test_calculate_start_page_num() {
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
            //given
            PageNavigation pageNavigation =
                    new PageNavigation(requestedPageNum, totalPagesNum, pageNavigationSize);

            //when
            int result = pageNavigation.getEndPageNum();

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

        @MethodSource
        @ParameterizedTest(name = "{index}. 요청된 페이지: {0}, 네비게이션 크기: {1}, 총 페이지 수: {2}, 예상 결과값: {3}")
        @DisplayName("(총 페이지 수 < 네비게이션의 끝 번호) 인 경우")
        void when_total_pages_lt_last_num_of_nav(int requestedPageNum, int pageNavigationSize, int totalPagesNum, int exptected) {
            //given
            PageNavigation pageNavigation =
                    new PageNavigation(requestedPageNum, totalPagesNum, pageNavigationSize);

            //when
            int result = pageNavigation.getEndPageNum();

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
}
