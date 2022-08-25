package com.bluesky.mainservice.repository.community.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class ReplySearchCondition {

    @Getter
    public static class ByOffset{

        private final Pageable pageable;
        private final String keyword;
        private final Integer boardDirectoryId;
        private final Integer boardCategoryId;

        @Builder
        private ByOffset(@NonNull Integer page,
                         @NonNull Integer pageSize,
                         @NonNull String keyword,
                         Integer boardDirectoryId,
                         Integer boardCategoryId) {
            this.pageable = PageRequest.of(page - 1, pageSize);
            this.keyword = keyword;
            this.boardDirectoryId = boardDirectoryId;
            this.boardCategoryId = boardCategoryId;
        }
    }
}
