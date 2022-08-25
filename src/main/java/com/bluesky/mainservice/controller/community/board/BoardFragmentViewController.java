package com.bluesky.mainservice.controller.community.board;

import com.bluesky.mainservice.controller.argument.LoginUser;
import com.bluesky.mainservice.controller.community.board.constant.ReplyOption;
import com.bluesky.mainservice.controller.community.board.dto.BoardResponseDto;
import com.bluesky.mainservice.controller.community.board.dto.BoardResponseDtoConverter;
import com.bluesky.mainservice.controller.community.board.dto.PageParam;
import com.bluesky.mainservice.repository.community.board.BoardRepository;
import com.bluesky.mainservice.repository.community.board.ReplyRepository;
import com.bluesky.mainservice.repository.community.board.constant.BoardState;
import com.bluesky.mainservice.service.community.board.BoardService;
import com.bluesky.mainservice.service.community.board.dto.ReplyDto;
import com.bluesky.mainservice.service.community.board.dto.ReplyFindCondition;
import com.bluesky.mainservice.service.community.board.exception.BoardNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Controller
public class BoardFragmentViewController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;

    @GetMapping("/reply")
    public String replyList(@RequestParam Long boardId,
                            PageParam param,
                            LoginUser loginUser,
                            Model model) {
        //게시글 작성자 id 조회
        UUID writerId = boardRepository.findUserUuidById(boardId, BoardState.ACTIVE)
                .orElseThrow(BoardNotFoundException::new);


        //댓글 조회
        ReplyFindCondition replyFindCondition = ReplyFindCondition.builder().boardId(boardId)
                .userId(loginUser == null ? null : loginUser.getId())
                .page(param.getPage())
                .pageSize(ReplyOption.PAGE_SIZE)
                .build();
        Page<ReplyDto> replyListPage = boardService.findReplyList(replyFindCondition);

        //총 댓글 수 조회
        int replyCount = replyRepository.countBoardReply(boardId);

        //replyList 변환
        List<BoardResponseDto.ReplyInsideView> replyList
                = BoardResponseDtoConverter.toReplyInsideViewList(replyListPage.getContent());

        //pageNavigation 변환
        BoardResponseDto.PageNavigation pageNavigation =
                new BoardResponseDto.PageNavigation(
                        param.getPage(), replyListPage.getTotalPages(), ReplyOption.PAGE_NAVIGATION_SIZE);

        if (loginUser != null) {
            model.addAttribute("loginUserId", loginUser.getId());
        }

        model.addAttribute("boardWriterId", writerId);
        model.addAttribute("pageNavigation", pageNavigation);
        model.addAttribute("replyList", replyList);
        model.addAttribute("replyCount", replyCount);
        return "community/board/board_content :: replyListFragment";
    }
}
