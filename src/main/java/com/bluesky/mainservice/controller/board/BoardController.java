package com.bluesky.mainservice.controller.board;

import com.bluesky.mainservice.repository.domain.Board;
import com.bluesky.mainservice.repository.dto.BoardDto;
import com.bluesky.mainservice.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static com.bluesky.mainservice.controller.board.Options.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public String list(Model model, PageParameter pageParam) {
        final int requestedPageNum = pageParam.getPage();
        Page<BoardDto> page = boardService.findPage(PageRequest.of(requestedPageNum - 1, PAGE_SIZE));
        List<BoardDto> boardList = page.getContent();

        final int totalPagesNum = page.getTotalPages();
        //페이지 네비게이션의 시작번호
        final int startPageNum = calculateStartPageNum(requestedPageNum, PAGE_NAVIGATION_SIZE);

        //페이지 네비게이션의 끝번호
        final int endPageNum = calculateEndPageNum(requestedPageNum, PAGE_NAVIGATION_SIZE, totalPagesNum);

        model.addAttribute("boardList", boardList);
        model.addAttribute("totalPagesNum", totalPagesNum);
        model.addAttribute("requestedPageNum", requestedPageNum);
        model.addAttribute("startPageNum", startPageNum);
        model.addAttribute("endPageNum", endPageNum);
        return "board/board_list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("boardForm", new BoardSaveForm());
        model.addAttribute("MAX_TITLE_SIZE", MAX_TITLE_SIZE);
        model.addAttribute("MAX_CONTENT_SIZE", MAX_CONTENT_SIZE);
        model.addAttribute("MAX_HTML_SIZE", MAX_HTML_SIZE);
        return "board/board_form";
    }

    @PostMapping("/create")
    public String create(@Valid BoardSaveForm form, BindingResult result) {
        if (result.hasErrors()) {
            log.info(result.getFieldErrors().toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }
        Board board = Board.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .createDateTime(LocalDateTime.now())
                .build();
        boardService.save(board);
        return "redirect:/board";
    }

    @GetMapping("/{boardId}")
    public String view(@PathVariable final long boardId, Model model) {
        BoardDto board = boardService.findOne(boardId);
        model.addAttribute("board", board);
        return "board/board_view";
    }

    @DeleteMapping("/{boardId}")
    public String delete(@PathVariable final long boardId) {
        boardService.delete(boardId);
        return "redirect:/board";
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
            //요청한 페이지 번호가 페이지네이션의 끝 번호라는 것을 의마한다
            return requestedPageNum;
        } else {
            //예) 요청한 페이지 번호가 87, 페이지네비게이션 크기가 10이라고 하면
            //마지막 번호는 90이 되야 하므로 (몫 + 1) * 10 을 한다
            int endOfPageNavigationNum = ((requestedPageNum / pageNavigationSize) + 1) * pageNavigationSize;

            //총 페이지 수가 네비게이션의 마지막 번호보다 작은 경우는
            //총 페이지 수가 네비게이션의 마지막 번호가 된다
            return Math.min(totalPagesNum, endOfPageNavigationNum);
        }
    }
}