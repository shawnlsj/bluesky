package com.bluesky.mainservice.service.community.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;

@Getter
public class ModifiedBoard {

    private final long id;
    private final int categoryId;
    private final String title;
    private final String content;
    private final UUID userId;

    @Builder
    private ModifiedBoard(@NonNull Long id,
                          @NonNull Integer categoryId,
                          @NonNull String title,
                          @NonNull String content,
                          @NonNull UUID userId) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
        this.userId = userId;
    }
}
