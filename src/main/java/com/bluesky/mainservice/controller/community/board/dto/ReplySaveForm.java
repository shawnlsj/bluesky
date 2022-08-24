package com.bluesky.mainservice.controller.community.board.dto;

import com.bluesky.mainservice.controller.community.board.validation.ReplyContent;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Positive;

@Getter
@Setter
public class ReplySaveForm {

    @Positive
    private Long boardId;

    @Positive
    private Long replyId;

    @ReplyContent
    private String content;
}
