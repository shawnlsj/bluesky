package com.bluesky.mainservice.repository.community.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class BoardDirectoryDto {

    private int id;
    private String name;

    @QueryProjection
    public BoardDirectoryDto(int id, String name) {
        this.id = id;
        this.name = name;
    }
}