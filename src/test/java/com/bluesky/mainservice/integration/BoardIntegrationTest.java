package com.bluesky.mainservice.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class BoardIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("게시글 첫 페이지 불러오기")
    void BoardIntegrationTest () throws Exception {
        //given

        //when
        mvc.perform(get("/board"));

        //then
    }

    @Test
    @DisplayName("정상 파라미터만 담긴 게시글 저장")
    void save_correct_board() throws Exception {
        //given
        String title = "title";
        String content = "content";
        //when
        ResultActions result = mvc.perform(get("/board/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("title", title)
                .param("content", content)
        );

        //then
        result.andExpect(status().isFound());
    }

    @Test
    @DisplayName("허용되지 않은 태그가 포함된 게시글 저장")
    void save_incorrect_board() throws Exception{
        //given
        String title = "title";
        String content = "<script>alert('HELLO_XSS')<br></script>";
        //when
        ResultActions result = mvc.perform(post("/board/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("title", title)
                .param("content", content)
        );

        //then
        result.andExpect(status().isBadRequest());
    }
}
