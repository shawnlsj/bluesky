package com.bluesky.mainservice.controller.community.board.dto;

import com.bluesky.mainservice.controller.community.board.validation.BoardContent;
import com.bluesky.mainservice.controller.community.board.validation.BoardTitle;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class BoardUpdateForm {

    @BoardTitle
    private String title;

    @BoardContent
    private String content;

    @NotNull
    @Positive
    private Integer categoryId;
}
