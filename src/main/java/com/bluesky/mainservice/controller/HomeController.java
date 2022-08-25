package com.bluesky.mainservice.controller;

import com.bluesky.mainservice.controller.community.board.constant.BoardOption;
import com.bluesky.mainservice.controller.community.board.dto.BoardResponseDto;
import com.bluesky.mainservice.controller.community.board.dto.BoardResponseDtoConverter;
import com.bluesky.mainservice.repository.community.board.BoardDirectoryRepository;
import com.bluesky.mainservice.repository.community.board.BoardRepository;
import com.bluesky.mainservice.repository.community.board.dto.BoardDirectoryWithBoardCount;
import com.bluesky.mainservice.repository.community.board.dto.BoardTitleNoUserDto;
import com.bluesky.mainservice.repository.community.board.dto.BoardSignatureSearchCondition;
import com.bluesky.mainservice.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final BoardRepository boardRepository;
    private final BoardDirectoryRepository boardDirectoryRepository;

    @GetMapping("/")
    public String home(HttpServletRequest request, HttpServletResponse response, Model model) {
        //인기 게시글 조회
        BoardSignatureSearchCondition.BoardTitleNoUserDtoSearchCondition bestBoardListSearchCondition =
                BoardSignatureSearchCondition.BoardTitleNoUserDtoSearchCondition.builder()
                        .page(1)
                        .pageSize(BoardOption.PAGE_SIZE_IN_HOME)
                        .minLikesCount(1)
                        .build();
        Page<BoardTitleNoUserDto> bestBoardPage = boardRepository.searchBoardTitleNoUserDto(bestBoardListSearchCondition);
        List<BoardResponseDto.SimpleBoardSignature> bestBoardList =
                BoardResponseDtoConverter.toSimpleBoardSignature(bestBoardPage.getContent());
        Collections.shuffle(bestBoardList);
        model.addAttribute("bestBoardList", bestBoardList);

        //최근 등록된 게시글 조회
        BoardSignatureSearchCondition.BoardTitleNoUserDtoSearchCondition recentBoardListSearchCondition =
                BoardSignatureSearchCondition.BoardTitleNoUserDtoSearchCondition.builder()
                .page(1)
                .pageSize(BoardOption.PAGE_SIZE_IN_HOME)
                .build();
        Page<BoardTitleNoUserDto> recentBoardPage = boardRepository.searchBoardTitleNoUserDto(recentBoardListSearchCondition);
        List<BoardResponseDto.SimpleBoardSignature> recentBoardList =
                BoardResponseDtoConverter.toSimpleBoardSignature(recentBoardPage.getContent());
        model.addAttribute("recentBoardList", recentBoardList);


        List<BoardDirectoryWithBoardCount> boardDirectoryWithBoardCountList = boardDirectoryRepository.countTodayBoard();
        List<BoardResponseDto.BoardCountStatus> boardCountStatusList =
                BoardResponseDtoConverter.toBoardCountStatus(boardDirectoryWithBoardCountList);
        model.addAttribute("boardCountStatusList", boardCountStatusList);

        //쿠키에서 화면에 출력할 메시지를 획득
        String message = CookieUtils.resolveMessageCookie(request);
        if (StringUtils.hasText(message)) {
            //메시지를 획득한 쿠키를 삭제
            CookieUtils.removeMessageCookie(response);

            //화면으로 메시지를 전달
            model.addAttribute("message", message);
        }
        return "index";
    }
}
