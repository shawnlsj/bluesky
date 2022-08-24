package com.bluesky.mainservice.controller.community.board;

import com.bluesky.mainservice.controller.argument.LoginUser;
import com.bluesky.mainservice.controller.community.board.constant.ReplyOption;
import com.bluesky.mainservice.controller.community.board.constant.SearchType;
import com.bluesky.mainservice.controller.community.board.dto.BoardSaveForm;
import com.bluesky.mainservice.controller.community.board.dto.BoardUpdateForm;
import com.bluesky.mainservice.controller.community.board.dto.PageParam;
import com.bluesky.mainservice.repository.community.board.*;
import com.bluesky.mainservice.repository.community.board.domain.BoardDirectory;
import com.bluesky.mainservice.repository.community.board.dto.*;
import com.bluesky.mainservice.repository.community.constant.CommunityType;
import com.bluesky.mainservice.service.community.board.BoardService;
import com.bluesky.mainservice.service.community.board.dto.BoardContent;
import com.bluesky.mainservice.service.community.board.dto.ReplyDto;
import com.bluesky.mainservice.service.community.board.dto.ReplyFindCondition;
import com.bluesky.mainservice.service.community.board.exception.BoardNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.bluesky.mainservice.controller.community.board.constant.BoardOption.*;
import static com.bluesky.mainservice.controller.community.board.dto.BoardResponseDto.*;
import static com.bluesky.mainservice.controller.community.board.dto.BoardResponseDtoConverter.*;
import static com.bluesky.mainservice.repository.community.board.dto.BoardSignatureSearchCondition.BoardTitleDtoSearchCondition;
import static com.bluesky.mainservice.repository.community.board.dto.BoardSignatureSearchCondition.BoardTitleNoUserDtoSearchCondition;

@Slf4j
@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final BoardDirectoryRepository boardDirectoryRepository;
    private final BoardCategoryRepository boardCategoryRepository;
    private final LikesRepository likesRepository;
    private final ReplyRepository replyRepository;

    @GetMapping(path = "/{communityType}/board/{directoryId:[0-9]+}")
    public String directoryBoardList(@PathVariable CommunityType communityType,
                                     @PathVariable Integer directoryId,
                                     @RequestParam(required = false) Integer categoryId,
                                     @RequestParam(name = "type", required = false) SearchType searchType,
                                     @RequestParam(required = false) String keyword,
                                     PageParam pageParam, Model model, HttpServletResponse response) throws IOException {
        if (!StringUtils.hasText(keyword)) {
            keyword = null;
        } else if (StringUtils.hasText(keyword) && searchType == null) {
            searchType = SearchType.TITLE_CONTENT;
        }


        String searchTitleCond = null;
        String searchContentCond = null;
        String searchWriterCond = null;
        BoardTitleDtoSearchCondition boardSearchCondition = null;
        int totalPagesNum = 0;
        if (searchType != null) {
            model.addAttribute("keyword", keyword);
            model.addAttribute("searchType", searchType.name().toLowerCase());
            switch (searchType) {
                case TITLE_CONTENT:
                    searchTitleCond = keyword;
                    searchContentCond = keyword;
                    break;
                case TITLE:
                    searchTitleCond = keyword;
                    break;
                case WRITER:
                    searchWriterCond = keyword;
                    break;
                case REPLY:
                    //댓글 조회 후 리스트를 모델에 추가
                    //검색 결과 수와 총 페이지 수를 댓글 페이지 에서 가져온다
                    Page<ReplySearchResultDto> replySearchResultDtoPage =
                            replyRepository.search(ReplySearchCondition.ByOffset.builder()
                                    .keyword(keyword)
                                    .page(pageParam.getPage())
                                    .pageSize(PAGE_NAVIGATION_SIZE)
                                    .boardDirectoryId(directoryId)
                                    .boardCategoryId(categoryId)
                                    .build());

                    //총 페이지 수를 댓글 검색 결과의 총 페이지 수로 설정
                    totalPagesNum = replySearchResultDtoPage.getTotalPages();

                    //댓글 목록을 dto 로 변환
                    List<ReplySearchResult> replySearchResultList =
                            toReplySearchResultList(replySearchResultDtoPage.getContent(), keyword);
                    model.addAttribute("replySearchResultList", replySearchResultList);

                    //댓글이 달린 게시글 id 를 리스트에 추가하여 검색 조건을 생성
                    List<Long> boardIdList = new ArrayList<>();
                    for (ReplySearchResultDto reply : replySearchResultDtoPage.getContent()) {
                        boardIdList.add(reply.getBoardId());
                    }
                    boardSearchCondition =
                            BoardTitleDtoSearchCondition.builder()
                                    .page(1)
                                    .pageSize(PAGE_NAVIGATION_SIZE)
                                    .boardIdList(boardIdList)
                                    .build();
                    break;
            }
        }


        //존재하지 않은 디렉토리를 요청하였으면 404 에러
        Optional<BoardDirectory> boardDirectory = boardDirectoryRepository.findById(directoryId);
        if (boardDirectory.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

        //boardDirectoryNavigation 생성
        BoardDirectoryNavigation boardDirectoryNavigation =
                createBoardDirectoryNavigation(communityType, directoryId);

        //댓글 검색이 아닌경우
        //게시글 검색 파라미터를 채워 검색 조건을 생성
        if (searchType != SearchType.REPLY) {
            boardSearchCondition = BoardTitleDtoSearchCondition.builder()
                    .page(pageParam.getPage())
                    .pageSize(PAGE_SIZE_IN_DIRECTORY)
                    .directoryId(directoryId)
                    .categoryId(categoryId)
                    .title(searchTitleCond)
                    .content(searchContentCond)
                    .nickname(searchWriterCond)
                    .build();
        }

        //게시글 조회 후 dto 변환
        Page<BoardTitleDto> boardSearchResultPage = boardRepository.searchBoardTitleDto(boardSearchCondition);
        List<BoardOutsideView> boardOutsideViewList =
                toBoardOutsideViewList(boardSearchResultPage.getContent(), keyword, searchType);

        if (searchType == SearchType.REPLY) {
            Map<Long, BoardOutsideView> boardMap = new HashMap<>();
            for (BoardOutsideView board : boardOutsideViewList) {
                boardMap.put(board.getId(), board);
            }
            model.addAttribute("boardMap", boardMap);
        } else {
            totalPagesNum = boardSearchResultPage.getTotalPages();
            model.addAttribute("boardOutsideViewList", boardOutsideViewList);
        }

        //카테고리 목록 조회
        List<BoardCategoryDto> boardCategoryList
                = boardCategoryRepository.findByBoardDirectoryId(directoryId);

        //pageNavigation 변환
        PageNavigation pageNavigation =
                new PageNavigation(
                        pageParam.getPage(), totalPagesNum, PAGE_NAVIGATION_SIZE);

        //게시글 랭킹 생성
        BoardRanking boardRanking = createBoardRanking(directoryId);
        model.addAttribute("boardRanking", boardRanking);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("boardCategoryList", boardCategoryList);
        model.addAttribute("boardDirectoryNavigation", boardDirectoryNavigation);
        model.addAttribute("pageNavigation", pageNavigation);
        return "community/board/board_list";
    }

    @GetMapping("/{communityType}/board/{directoryId:[0-9]+}/write")
    public String writeForm(@PathVariable CommunityType communityType,
                            @PathVariable Integer directoryId,
                            Model model, HttpServletResponse response) throws IOException {

        //존재하지 않는 디렉토리 id면 404 에러
        Optional<BoardDirectory> boardDirectory = boardDirectoryRepository.findById(directoryId);
        if (boardDirectory.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

        //boardDirectoryNavigation 생성
        BoardDirectoryNavigation boardDirectoryNavigation =
                createBoardDirectoryNavigation(communityType, directoryId);

        //카테고리 목록 조회
        List<BoardCategoryDto> boardCategoryList
                = boardCategoryRepository.findByBoardDirectoryId(directoryId);

        //게시글 랭킹 생성
        BoardRanking boardRanking = createBoardRanking(directoryId);

        BoardSaveForm boardSaveForm = new BoardSaveForm(communityType, directoryId);
        model.addAttribute("boardRanking", boardRanking);
        model.addAttribute("boardDirectoryNavigation", boardDirectoryNavigation);
        model.addAttribute("boardCategoryList", boardCategoryList);
        model.addAttribute("boardForm", boardSaveForm);
        return "community/board/board_form";
    }

    @GetMapping("/{communityType}/board/{directoryId:[0-9]+}/{boardId:[0-9]+}/modify")
    public String modifyForm(@PathVariable CommunityType communityType,
                             @PathVariable Integer directoryId,
                             @PathVariable Long boardId,
                             LoginUser loginUser,
                             Model model, HttpServletResponse response) throws IOException {

        //존재하지 않는 디렉토리 id면 404 에러
        Optional<BoardDirectory> boardDirectory = boardDirectoryRepository.findById(directoryId);
        if (boardDirectory.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

        //boardDirectoryNavigation 생성
        BoardDirectoryNavigation boardDirectoryNavigation =
                createBoardDirectoryNavigation(communityType, directoryId);

        //게시글 조회
        //사용자 본인이 작성한 글이 아니면 예외 발생
        BoardContent boardContent = boardService.findBoard(boardId, false);
        if (!boardContent.getUserId().equals(loginUser.getId())) {
            throw new BoardNotFoundException();
        }

        //카테고리 목록 조회
        List<BoardCategoryDto> boardCategoryList
                = boardCategoryRepository.findByBoardDirectoryId(directoryId);

        //board 변환
        BoardModifyView board = toBoardModifyView(boardContent);

        //게시글 랭킹 생성
        BoardRanking boardRanking = createBoardRanking(directoryId);
        model.addAttribute("boardRanking", boardRanking);
        model.addAttribute("boardDirectoryNavigation", boardDirectoryNavigation);
        model.addAttribute("boardCategoryList", boardCategoryList);
        model.addAttribute("boardForm", new BoardUpdateForm());
        model.addAttribute("board", board);
        return "community/board/board_form";
    }


    @GetMapping("/{communityType}/board/{directoryId:[0-9]+}/{boardId:[0-9]+}")
    public String viewContent(@PathVariable CommunityType communityType,
                              @PathVariable Integer directoryId,
                              @PathVariable Long boardId, LoginUser loginUser, HttpServletResponse response, Model model) throws IOException {

        //존재하지 않는 디렉토리 id면 404 에러
        Optional<BoardDirectory> boardDirectory = boardDirectoryRepository.findById(directoryId);
        if (boardDirectory.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

        //boardDirectoryNavigation 생성
        BoardDirectoryNavigation boardDirectoryNavigation =
                createBoardDirectoryNavigation(communityType, directoryId);

        //게시글 조회
        BoardContent boardContent = boardService.findBoard(boardId, true);

        //로그인한 유저면 게시글의 likes 를 눌렀는지 확인
        boolean boardLikesClicked;
        if (loginUser != null) {
            boardLikesClicked = likesRepository.existsByBoardIdAndUserId(boardId, loginUser.getId());
        } else {
            boardLikesClicked = false;
        }

        //댓글 첫 페이지 조회
        Page<ReplyDto> replyListPage = boardService
                .findReplyList(ReplyFindCondition.builder()
                        .boardId(boardId)
                        .userId(loginUser == null ? null : loginUser.getId())
                        .page(1)
                        .pageSize(ReplyOption.PAGE_SIZE)
                        .build());

        //총 댓글 수 조회
        int replyCount = replyRepository.countBoardReply(boardId);

        //board 변환
        BoardInsideView board =
                toBoardInsideViewList(boardContent, boardLikesClicked);

        //replyList 변환
        List<ReplyInsideView> replyList =
                toReplyInsideViewList(replyListPage.getContent());

        //pageNavigation 변환
        PageNavigation pageNavigation =
                new PageNavigation(1, replyListPage.getTotalPages(), ReplyOption.PAGE_NAVIGATION_SIZE);

        if (loginUser != null) {
            model.addAttribute("loginUserId", loginUser.getId());
        }

        //게시글 랭킹 생성
        BoardRanking boardRanking = createBoardRanking(directoryId);
        model.addAttribute("boardRanking", boardRanking);
        model.addAttribute("board", board);
        model.addAttribute("boardWriterId", board.getUserId());
        model.addAttribute("boardDirectoryNavigation", boardDirectoryNavigation);
        model.addAttribute("replyList", replyList);
        model.addAttribute("replyCount", replyCount);
        model.addAttribute("pageNavigation", pageNavigation);
        return "community/board/board_content";
    }

    @GetMapping("/search")
    public String searchResult(@RequestParam(name = "type", required = false, defaultValue = "title_content") SearchType searchType,
                               @RequestParam(required = false) String keyword,
                               Model model,
                               PageParam pageParam) {
        if (!StringUtils.hasText(keyword)) {
            model.addAttribute("totalCount", 0);
            model.addAttribute("keyword", "");
            return "search_result";
        }

        String title = null;
        String content = null;
        String nickname = null;
        BoardSearchCondition.ByOffset boardSearchCondition = null;
        int totalPagesNum = 0;
        long totalCount = 0;
        switch (searchType) {
            case TITLE_CONTENT:
                title = keyword;
                content = keyword;
                break;
            case TITLE:
                title = keyword;
                break;
            case WRITER:
                nickname = keyword;
                break;
            case REPLY:
                //댓글 조회 후 리스트를 모델에 추가
                //검색 결과 수와 총 페이지 수를 댓글 페이지 에서 가져온다
                Page<ReplySearchResultDto> replySearchResultDtoPage =
                        replyRepository.search(ReplySearchCondition.ByOffset.builder()
                                .keyword(keyword)
                                .page(pageParam.getPage())
                                .pageSize(PAGE_NAVIGATION_SIZE)
                                .build());
                totalPagesNum = replySearchResultDtoPage.getTotalPages();
                totalCount = replySearchResultDtoPage.getTotalElements();

                List<ReplySearchResult> replySearchResultList =
                        toReplySearchResultList(replySearchResultDtoPage.getContent(), keyword);
                model.addAttribute("replySearchResultList", replySearchResultList);

                //댓글이 달린 게시글 id 를 리스트에 추가하여 검색 조건을 생성
                List<Long> boardIdList = new ArrayList<>();
                for (ReplySearchResultDto reply : replySearchResultDtoPage.getContent()) {
                    boardIdList.add(reply.getBoardId());
                }
                boardSearchCondition =
                        BoardSearchCondition.ByOffset.builder()
                                .page(1)
                                .pageSize(PAGE_NAVIGATION_SIZE)
                                .boardIdList(boardIdList)
                                .build();
                break;
        }

        //댓글 검색이 아닌경우
        //게시글 검색 파라미터를 채워 검색 조건을 생성
        if (searchType != SearchType.REPLY) {
            boardSearchCondition =
                    BoardSearchCondition.ByOffset.builder()
                            .page(pageParam.getPage())
                            .pageSize(PAGE_NAVIGATION_SIZE)
                            .title(title)
                            .content(content)
                            .nickname(nickname)
                            .build();
        }

        //게시글 조회 후 dto 로 변환
        Page<BoardTextContentDto> boardSearchResultPage = boardRepository.searchBoardTextContentDto(boardSearchCondition);
        List<BoardSearchResult> boardSearchResultList =
                toBoardSearchResultList(boardSearchResultPage.getContent(), keyword, searchType);

        //댓글 검색인 경우 게시글을 Map 에 담아서 모델에 추가
        if (searchType == SearchType.REPLY) {
            Map<Long, BoardSearchResult> boardMap = new HashMap<>();
            for (BoardSearchResult board : boardSearchResultList) {
                boardMap.put(board.getId(), board);
            }
            model.addAttribute("boardMap", boardMap);
        } else {
            //댓글 검색이 아닌경우 게시글 List 를 모델에 추가
            //검색 결과 수와 총 페이지 수를 게시글 페이지 에서 가져온다
            totalCount = boardSearchResultPage.getTotalElements();
            totalPagesNum = boardSearchResultPage.getTotalPages();
            model.addAttribute("boardSearchResultList", boardSearchResultList);
        }

        PageNavigation pageNavigation =
                new PageNavigation(pageParam.getPage(), totalPagesNum, PAGE_NAVIGATION_SIZE);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pageNavigation", pageNavigation);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType.name().toLowerCase());
        return "search_result";
    }

    private BoardDirectoryNavigation createBoardDirectoryNavigation(CommunityType communityType, int boardDirectoryId) {
        //디렉토리 목록 조회
        List<BoardDirectoryDto> boardDirectoryList =
                boardDirectoryRepository.findDtoByCommunityType(communityType);

        //boardDirectoryNavigation 변환
        return toBoardDirectoryNavigation(boardDirectoryList, boardDirectoryId);
    }

    private BoardRanking createBoardRanking(int boardDirectoryId) {
        //좋아요 랭킹 조회
        BoardTitleNoUserDtoSearchCondition likesCountRankingSearchCondition =
                BoardTitleNoUserDtoSearchCondition.builder()
                        .page(1)
                        .pageSize(DAILY_RANKING_PAGE_SIZE)
                        .boardDirectoryId(boardDirectoryId)
                        .minLikesCount(1)
                        .since(LocalDateTime.now().minusHours(24))
                        .until(LocalDateTime.now())
                        .build();
        likesCountRankingSearchCondition.orderByLikesCount();
        Page<BoardTitleNoUserDto> likesCountRankingBoardPage =
                boardRepository.searchBoardTitleNoUserDto(likesCountRankingSearchCondition);

        //조회된 리스트를 정렬 후 dto 로 변환
        List<BoardTitleNoUserDto> likesCountRankingBoardPageContent =
                likesCountRankingBoardPage.stream()
                        .sorted(Comparator.comparingInt(BoardTitleNoUserDto::getLikesCount)
                                .thenComparingLong(BoardTitleNoUserDto::getId))
                        .collect(Collectors.toList());
        List<SimpleBoardSignature> likesCountRankingBoardList =
                toSimpleBoardSignature(likesCountRankingBoardPageContent);

        //조회수 랭킹 조회
        BoardTitleNoUserDtoSearchCondition viewCountRankingSearchCondition =
                BoardTitleNoUserDtoSearchCondition.builder()
                        .page(1)
                        .pageSize(DAILY_RANKING_PAGE_SIZE)
                        .boardDirectoryId(boardDirectoryId)
                        .minViewCount(1)
                        .since(LocalDateTime.now().minusHours(24))
                        .until(LocalDateTime.now())
                        .build();
        viewCountRankingSearchCondition.orderByViewCount();
        Page<BoardTitleNoUserDto> viewCountRankingBoardPage =
                boardRepository.searchBoardTitleNoUserDto(viewCountRankingSearchCondition);

        //조회된 리스트를 정렬 후 dto 로 변환
        List<BoardTitleNoUserDto> viewCountRankingBoardPageContent =
                viewCountRankingBoardPage.stream()
                        .sorted(Comparator.comparingInt(BoardTitleNoUserDto::getViewCount)
                                .thenComparingLong(BoardTitleNoUserDto::getId))
                        .collect(Collectors.toList());
        List<SimpleBoardSignature> viewCountRankingBoardList =
                toSimpleBoardSignature(viewCountRankingBoardPageContent);

        //두 리스트를 묶어서 반환
        return new BoardRanking(likesCountRankingBoardList, viewCountRankingBoardList);
    }
}