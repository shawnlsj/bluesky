package com.bluesky.mainservice.unit;

import com.bluesky.mainservice.config.filter.XssFilter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class XssFilterTest {

    private XssFilter filter = new XssFilter();
    private MockHttpServletRequest request = new MockHttpServletRequest();
    private MockHttpServletResponse response = new MockHttpServletResponse();
    private MockFilterChain filterChain = new MockFilterChain();

    @BeforeEach
    void init() {
        request.setRequestURI("/board");
        request.setMethod("POST");

        filter.init(new FilterConfig() {
            @Override
            public String getFilterName() {
                return null;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public String getInitParameter(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return null;
            }
        });
    }

    @Test
    @Order(1)
    @DisplayName("허용하지 않은 태그에 대한 필터링 테스트")
    void when_contains_not_allowed_tag() throws ServletException, IOException {
        //given
        String content = "<script>alert('HELLO_XSS')</script>";
        request.addParameter("content", content);

        //when
        filter.doFilter(request, response, filterChain);

        //then
        String filteredContent = filterChain.getRequest().getParameter("content");
        Assertions.assertThat(filteredContent).contains("<!-- Not Allowed Tag Filtered");
    }

    @MethodSource
    @ParameterizedTest
    @Order(2)
    @DisplayName("허용한 태그에 대한 필터링 테스트")
    void when_contains_allowed_tag(String tag) throws ServletException, IOException {
        //given
        String content = tag + "content";
        request.addParameter("content", content);

        //when
        filter.doFilter(request, response, filterChain);

        //then
        String filteredContent = filterChain.getRequest().getParameter("content");
        Assertions.assertThat(filteredContent).doesNotContain("<!-- Not Allowed Tag Filtered");
    }

    static Stream<String> when_contains_allowed_tag() {
        List<String> allowedTagList = new ArrayList<>();
        allowedTagList.add("div");
        allowedTagList.add("p");
        allowedTagList.add("span");
        allowedTagList.add("br");
        allowedTagList.add("b");
        allowedTagList.add("i");
        allowedTagList.add("u");
        allowedTagList.add("s");
        allowedTagList.add("strike");
        allowedTagList.add("strong");
        allowedTagList.add("font");
        return allowedTagList.stream().map(s -> "<" + s + ">");
    }

    @MethodSource
    @ParameterizedTest
    @Order(3)
    @DisplayName("허용되지 않은 속성에 대한 필터링 테스트")
    void when_contains_not_allowed_attribute(String content) throws ServletException, IOException {
        //given
        request.addParameter("content", content);

        //when
        filter.doFilter(request, response, filterChain);

        //then
        String filteredContent = filterChain.getRequest().getParameter("content");
        Assertions.assertThat(filteredContent).contains("<!-- Not Allowed Attribute Filtered");
    }

    static List<String> when_contains_not_allowed_attribute() {
        List<String> contentList = new ArrayList<>();
        contentList.add("<div onclick=\"alert('xss')\">content</div>");
        contentList.add("<div style=\"font-size:10px\">content</div>");
        return contentList;
    }

    @MethodSource
    @ParameterizedTest
    @Order(4)
    @DisplayName("허용한 속성에 대한 필터링 테스트")
    void when_contains_allowed_attribute(String content) throws ServletException, IOException {
        //given
        request.addParameter("content", content);

        //when
        filter.doFilter(request, response, filterChain);

        //then
        String filteredContent = filterChain.getRequest().getParameter("content");
        Assertions.assertThat(filteredContent).doesNotContain("<!-- Not Allowed Attribute Filtered");
    }

    static List<String> when_contains_allowed_attribute() {
        List<String> contentList = new ArrayList<>();
        contentList.add("<font size=\"3\">content</font>");
        contentList.add("<font size=\"3\" style=\"\">content</font>");
        contentList.add("<div style=\"\">content</div>");
        return contentList;
    }
}

