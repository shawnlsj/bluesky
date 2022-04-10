package com.bluesky.mainservice.dto;

import com.bluesky.mainservice.domain.Board;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardDto {
    private final Long id;
    private final LocalDateTime createDateTime;
    private final String title;
    private final String content;

    private BoardDto(Board board) {
        this.id = board.getId();
        this.createDateTime = board.getCreateDateTime();
        this.title = board.getTitle();
        this.content = board.getContent();
    }

    public static BoardDto build(Board board) {
        return new BoardDto(board);
    }
}
