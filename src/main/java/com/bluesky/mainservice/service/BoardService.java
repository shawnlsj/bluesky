package com.bluesky.mainservice.service;

import com.bluesky.mainservice.domain.Board;
import com.bluesky.mainservice.dto.BoardDto;
import com.bluesky.mainservice.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository repository;

    @Transactional(readOnly = true)
    public List<BoardDto> findBoardList() {
        List<Board> boardList = repository.findAll();
        return boardList.stream()
                   .map(BoardDto::new)
                   .collect(Collectors.toList());
    }

    public Long save(Board board) {
        repository.save(board);
        return board.getId();
    }
}
