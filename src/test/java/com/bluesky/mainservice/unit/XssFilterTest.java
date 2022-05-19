package com.bluesky.mainservice.unit;

import com.bluesky.mainservice.config.filter.XssFilter;
import com.navercorp.lucy.security.xss.servletfilter.XssEscapeFilterConfig;
import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;
import com.nhncorp.lucy.security.xss.LucyXssFilter;
import com.nhncorp.lucy.security.xss.XssSaxFilter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class XssFilterTest {

    private XssFilter filter = new XssFilter();
    private MockHttpServletRequest request = new MockHttpServletRequest();
    private MockHttpServletResponse response = new MockHttpServletResponse();
    private MockFilterChain filterChain = new MockFilterChain();

    @Test
    @Order(1)
    @DisplayName("허용하지 않은 태그에 대한 필터링 테스트")
    void filter_not_allowed_tag() throws ServletException, IOException {

        //given
        String content = "<script>alert('HELLO_XSS')</script>";
        request.setRequestURI("/board/create");
        request.addParameter("content", content);

        //when
        filter.doFilter(request, response, filterChain);

        //then
        String filteredContent = filterChain.getRequest().getParameter("content");
        Assertions.assertThat(filteredContent).contains("<!-- Not Allowed Tag Filtered");
        log.info("before : {}", content);
        log.info("after : {}", filteredContent);
    }

    @Test
    @Order(2)
    @DisplayName("허용한 태그에 대한 필터링 테스트")
    void filter_allowed_tag() throws ServletException, IOException {
        //given
        String content = "<br><div></div><b></b>";
        request.setRequestURI("/board/create");
        request.addParameter("content", content);

        //when
        filter.doFilter(request, response, filterChain);

        //then
        String filteredContent = filterChain.getRequest().getParameter("content");
        Assertions.assertThat(filteredContent).doesNotContain("<!-- Not Allowed Tag Filtered");
        log.info("before : {}", content);
        log.info("after : {}", filteredContent);
    }

    @Test
    @Order(3)
    @DisplayName("허용하지 않은 속성 대한 필터링 테스트")
    void filter_not_allowed_attribute() throws ServletException, IOException {
        //given
        String content = "<div onclick=\"alert('xss')\"></div>";
        request.setRequestURI("/board/create");
        request.addParameter("content", content);

        //when
        filter.doFilter(request, response, filterChain);

        //then
        String filteredContent = filterChain.getRequest().getParameter("content");
        Assertions.assertThat(filteredContent).contains("<!-- Not Allowed Attribute Filtered");
        log.info("before : {}", content);
        log.info("after : {}", filteredContent);
    }

    @Test
    @Order(4)
    @DisplayName("허용한 속성에 대한 필터링 테스트")
    void filter_allowed_attribute() throws ServletException, IOException {
        //given
        String content = "<font size=\"10\">hello</font>";
        request.setRequestURI("/board/create");
        request.addParameter("content", content);

        //when
        filter.doFilter(request, response, filterChain);

        //then
        String filteredContent = filterChain.getRequest().getParameter("content");
        Assertions.assertThat(filteredContent).doesNotContain("<!-- Not Allowed Attribute Filtered");
        log.info("before : {}", content);
        log.info("after : {}", filteredContent);
    }
}

