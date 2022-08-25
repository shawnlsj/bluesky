package com.bluesky.mainservice.controller.community;


import com.bluesky.mainservice.controller.community.board.constant.BoardOption;
import com.bluesky.mainservice.controller.community.board.dto.BoardResponseDto;
import com.bluesky.mainservice.controller.community.board.dto.BoardResponseDtoConverter;
import com.bluesky.mainservice.repository.community.board.BoardDirectoryRepository;
import com.bluesky.mainservice.repository.community.board.BoardRepository;
import com.bluesky.mainservice.repository.community.board.dto.BoardDirectoryDto;
import com.bluesky.mainservice.repository.community.board.dto.BoardSignatureSearchCondition;
import com.bluesky.mainservice.repository.community.board.dto.BoardTitleNoUserDto;
import com.bluesky.mainservice.repository.community.constant.CommunityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommunityHomeController {

    private final BoardDirectoryRepository boardDirectoryRepository;
    private final BoardRepository boardRepository;

    @GetMapping("/{communityType}")
    public String communityHome(@PathVariable CommunityType communityType, Model model) {
        //인기 게시글 조회
        BoardSignatureSearchCondition.BoardTitleNoUserDtoSearchCondition bestBoardListSearchCondition =
                BoardSignatureSearchCondition.BoardTitleNoUserDtoSearchCondition.builder()
                        .page(1)
                        .pageSize(BoardOption.PAGE_SIZE_IN_HOME)
                        .communityType(communityType)
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
                        .communityType(communityType)
                        .build();
        Page<BoardTitleNoUserDto> recentBoardPage = boardRepository.searchBoardTitleNoUserDto(recentBoardListSearchCondition);
        List<BoardResponseDto.SimpleBoardSignature> recentBoardList =
                BoardResponseDtoConverter.toSimpleBoardSignature(recentBoardPage.getContent());
        model.addAttribute("recentBoardList", recentBoardList);

        //커뮤니티에 해당하는 디렉토리 목록을 조회
        List<BoardDirectoryDto> boardDirectoryList =
                boardDirectoryRepository.findDtoByCommunityType(communityType);

        //boardDirectoryNavigation 변환
        BoardResponseDto.BoardDirectoryNavigation boardDirectoryNavigation =
                BoardResponseDtoConverter.toBoardDirectoryNavigation(boardDirectoryList, null);

        model.addAttribute("boardDirectoryNavigation", boardDirectoryNavigation);
        return "community/community_home";
    }
}
