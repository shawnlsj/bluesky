package com.bluesky.mainservice.service;

import com.bluesky.mainservice.domain.Board;
import com.bluesky.mainservice.dto.BoardDto;
import com.bluesky.mainservice.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository repository;

    @Transactional(readOnly = true)
    public List<BoardDto> findList() {
        List<Board> boardList = repository.findAll();
        return boardList.stream()
                   .map(BoardDto::build)
                   .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BoardDto findOne(Long boardId) {
        Optional<Board> result = repository.findById(boardId);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 게시물 입니다.");
        }
        Board board = result.get();
        BoardDto boardDto = BoardDto.build(board);
        return boardDto;
    }

    public Long save(Board board) {
        repository.save(board);
        return board.getId();
    }

    public void delete(long boardId) {
        try {
            repository.deleteById(boardId);
        } catch (EmptyResultDataAccessException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않거나 이미 삭제된 게시물입니다.");
        }
    }
}
