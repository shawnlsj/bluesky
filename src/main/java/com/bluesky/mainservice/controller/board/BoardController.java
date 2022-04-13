package com.bluesky.mainservice.controller.board;

import com.bluesky.mainservice.domain.Board;
import com.bluesky.mainservice.dto.BoardDto;
import com.bluesky.mainservice.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
        model.addAttribute("boardForm", new BoardForm());
        return "board/board_form";
    }

    @PostMapping("/create")
    public String create(@Valid BoardForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "board/board_form";
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
