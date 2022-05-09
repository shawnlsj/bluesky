package com.bluesky.mainservice.controller.board;

import com.bluesky.mainservice.domain.Board;
import com.bluesky.mainservice.dto.BoardDto;
import com.bluesky.mainservice.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public String list(Model model) {
        List<BoardDto> boardList = boardService.findList();
        Collections.reverse(boardList);
        model.addAttribute("boardList", boardList);
        return "board/board_list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("boardForm", new BoardSaveForm());
        model.addAttribute("MAX_TITLE_SIZE", Options.MAX_TITLE_SIZE);
        model.addAttribute("MAX_CONTENT_SIZE", Options.MAX_CONTENT_SIZE);
        model.addAttribute("MAX_HTML_SIZE", Options.MAX_HTML_SIZE);
        return "board/board_form";
    }

    @PostMapping("/create")
    public String create(@Valid BoardSaveForm form, BindingResult result) {
        if (result.hasErrors()) {
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
}
