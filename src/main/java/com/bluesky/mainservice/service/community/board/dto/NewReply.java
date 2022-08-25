package com.bluesky.mainservice.service.community.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;

@Getter
public class NewReply {

    private final UUID userId;
    private final Long boardId;
    private final Long replyId;
    private final String content;

    @Builder
    private NewReply(@NonNull UUID userId,
                     @NonNull Long boardId,
                     Long replyId,
                     @NonNull String content) {
        this.userId = userId;
        this.boardId = boardId;
        this.replyId = replyId;
        this.content = content;
    }
}
