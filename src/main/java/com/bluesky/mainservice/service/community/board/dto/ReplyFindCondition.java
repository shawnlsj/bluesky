package com.bluesky.mainservice.service.community.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Getter
public class ReplyFindCondition {

    private final Long boardId;
    private final UUID userId;
    private final Pageable pageable;

    @Builder
    private ReplyFindCondition(@NonNull Long boardId,
                               UUID userId,
                               @NonNull Integer page,
                               @NonNull Integer pageSize) {
        this.boardId = boardId;
        this.userId = userId;
        pageable = PageRequest.of(page - 1, pageSize);
    }
}
