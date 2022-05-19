package com.bluesky.mainservice.repository.dto;

import com.bluesky.mainservice.repository.domain.Board;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardDto {
    private final Long id;
    private final LocalDateTime createDateTime;
    private final String title;
    private final String content;

    @QueryProjection
    public BoardDto(Long id, LocalDateTime createDateTime, String title, String content) {
        this.id = id;
        this.createDateTime = createDateTime;
        this.title = title;
        this.content = content;
    }

    private BoardDto(Board board) {
        this.id = board.getId();
        this.createDateTime = board.getCreateDateTime();
        this.title = board.getTitle();
        this.content = board.getContent();
    }

    public static BoardDto createFromBoard(Board board) {
        return new BoardDto(board);
    }
}
