package com.bluesky.mainservice.integration;

import com.bluesky.mainservice.repository.community.board.BoardRepository;
import com.bluesky.mainservice.repository.community.board.ReplyRepository;
import com.bluesky.mainservice.repository.community.board.domain.*;
import com.bluesky.mainservice.repository.community.constant.CommunityType;
import com.bluesky.mainservice.repository.user.UserRepository;
import com.bluesky.mainservice.repository.user.constant.AccountType;
import com.bluesky.mainservice.repository.user.constant.RoleType;
import com.bluesky.mainservice.repository.user.domain.User;
import com.bluesky.mainservice.service.community.board.BoardService;
import com.bluesky.mainservice.service.community.board.dto.NewBoard;
import com.bluesky.mainservice.service.community.board.dto.NewReply;
import com.bluesky.mainservice.service.community.board.exception.BoardNotFoundException;
import com.bluesky.mainservice.service.community.board.exception.ReplyNotFoundException;
import com.bluesky.mainservice.service.user.security.LoginUserDetails;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class BoardApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BoardService boardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ReplyRepository replyRepository;

    private User loginUser;

    private Board savedBoard;

    private Reply savedReply;

    @BeforeEach
    void init() {
        //로그인 유저 인증 정보 초기화
        User user = User.builder().nickname("tester")
                .email("hello@hello.co.kr")
                .accountType(AccountType.ORIGINAL)
                .password("1234")
                .build();
        userRepository.save(user);
        loginUser = user;
        Object principal = new LoginUserDetails(loginUser.getUuid(), Collections.EMPTY_LIST);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(RoleType.USER.name()));
        TestSecurityContextHolder.setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, "", authorities));

        //게시글 저장
        NewBoard newBoard = NewBoard.builder()
                .communityType(CommunityType.PROGRAMMING)
                .directoryId(1)
                .categoryId(1)
                .title("title")
                .content("content")
                .userId(loginUser.getUuid())
                .build();
        Long boardId = boardService.saveBoard(newBoard);
        savedBoard = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);

        //댓글 저장
        NewReply newReply = NewReply.builder()
                .content("content")
                .userId(loginUser.getUuid())
                .boardId(savedBoard.getId())
                .build();
        Long replyId = boardService.saveReply(newReply);
        savedReply = replyRepository.findById(replyId)
                .orElseThrow(ReplyNotFoundException::new);
    }

    @Order(1)
    @Test
    @DisplayName("게시글 저장 테스트")
    void test_save_board() throws Exception {
        //given
        String title = "title";
        String content = "content";
        String communityType = "programming";
        String directoryId = "1";
        String categoryId = "1";

        //when
        ResultActions result = mvc.perform(post("/board")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("title", title)
                .param("content", content)
                .param("communityType", communityType)
                .param("directoryId", directoryId)
                .param("categoryId", categoryId)
        );

        //then
        result.andExpect(status().isOk());
    }

    @Order(2)
    @Test
    @DisplayName("게시글 수정 테스트")
    void test_modify_board() throws Exception {
        //given
        String boardId = savedBoard.getId().toString();
        String categoryId = savedBoard.getBoardCategory().getId().toString();
        String modifiedTitle = "hello";
        String modifiedContent = "world";

        //when
        ResultActions result = mvc.perform(put("/board" + "/" + boardId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("title", modifiedTitle)
                .param("content", modifiedContent)
                .param("categoryId", categoryId)
        );

        //then
        result.andExpect(status().isOk());
    }

    @Order(3)
    @Test
    @DisplayName("게시글 삭제 테스트")
    void test_delete_board() throws Exception {
        //given
        String boardId = savedBoard.getId().toString();

        //when
        ResultActions result = mvc.perform(delete("/board" + "/" + boardId));

        //then
        result.andExpect(status().isOk());
    }

    @Order(4)
    @Test
    @DisplayName("댓글 저장 테스트")
    void test_save_reply() throws Exception {
        //given
        String boardId = savedBoard.getId().toString();
        String content = "content";

        //when
        ResultActions result = mvc.perform(post("/reply")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("boardId", boardId)
                .param("content", content));

        //then
        result.andExpect(status().isOk());
    }

    @Order(5)
    @Test
    @DisplayName("답글 저장 테스트")
    void test_save_nested_reply() throws Exception {
        //given
        String content = "hello world";
        String boardId = savedBoard.getId().toString();
        String replyId = savedReply.getId().toString();

        //when
        ResultActions result = mvc.perform(post("/reply")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("boardId", boardId)
                .param("replyId", replyId)
                .param("content", content)
        );

        //then
        result.andExpect(status().isOk());
    }

    @Order(6)
    @Test
    @DisplayName("댓글 수정 테스트")
    void test_modify_reply() throws Exception {
        //given
        String replyId = savedReply.getId().toString();
        String modifiedContent = "hello world";

        //when
        ResultActions result = mvc.perform(put("/reply" + "/" + replyId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("content", modifiedContent)
        );

        //then
        result.andExpect(status().isOk());
    }

    @Order(7)
    @Test
    @DisplayName("댓글 삭제 테스트")
    void test_delete_reply() throws Exception {
        //given
        String replyId = savedReply.getId().toString();

        //when
        ResultActions result = mvc.perform(delete("/reply" + "/" + replyId));

        //then
        result.andExpect(status().isOk());
    }

    @Order(8)
    @Test
    @DisplayName("게시글 좋아요 켜키 테스트")
    void test_turn_on_board_likes() throws Exception {
        //given
        String boardId = savedBoard.getId().toString();

        //when
        ResultActions result = mvc.perform(get("/board/toggle-likes")
                .param("boardId", boardId));

        //then
        result.andExpect(status().isOk());
    }

    @Order(9)
    @Test
    @DisplayName("게시글 좋아요 끄기 테스트")
    void test_turn_off_board_likes() throws Exception {
        //given
        String boardId = savedBoard.getId().toString();

        //when
        boardService.toggleBoardLikes(savedBoard.getId(), loginUser.getUuid());
        ResultActions result = mvc.perform(get("/board/toggle-likes")
                .param("boardId", boardId));

        //then
        result.andExpect(status().isOk());
    }

    @Order(10)
    @Test
    @DisplayName("댓글 좋아요 켜기 테스트")
    void test_turn_on_reply_likes() throws Exception {
        //given
        String replyId = savedReply.getId().toString();

        //when
        ResultActions result = mvc.perform(get("/reply/toggle-likes")
                .param("replyId", replyId));

        //then
        result.andExpect(status().isOk());
    }

    @Order(11)
    @Test
    @DisplayName("댓글 좋아요 끄기 테스트")
    void test_turn_off_reply_likes() throws Exception {
        //given
        String replyId = savedReply.getId().toString();

        //when
        boardService.toggleReplyLikes(savedReply.getId(), loginUser.getUuid());
        ResultActions result = mvc.perform(get("/reply/toggle-likes")
                .param("replyId", replyId));

        //then
        result.andExpect(status().isOk());
    }
}
