package com.bluesky.mainservice.service.board;

import com.bluesky.mainservice.repository.board.BoardRepository;
import com.bluesky.mainservice.repository.domain.Board;
import com.bluesky.mainservice.repository.dto.BoardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository repository;

    @Transactional(readOnly = true)
    public Page<BoardDto> findPage(Pageable pageable) {
        Page<BoardDto> page = repository.findDtoPage(pageable);
        if (page.getSize() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "페이지가 존재하지 않습니다.");
        }
        return page;
    }

    @Transactional(readOnly = true)
    public BoardDto findOne(Long boardId) {
        Optional<Board> result = repository.findById(boardId);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 게시물 입니다.");
        }
        Board board = result.get();
        return BoardDto.createFromBoard(board);
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
