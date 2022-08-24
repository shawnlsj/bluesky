package com.bluesky.mainservice.controller.community.board.dto;

import com.bluesky.mainservice.controller.community.board.validation.ReplyContent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyUpdateForm {

    @ReplyContent
    private String content;
}
