package com.bluesky.mainservice.repository.community.board.dto;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class BoardSearchCondition {

    @Getter
    public static class ByOffset {

        private final Pageable pageable;
        private final String nickname;
        private final String title;
        private final String content;
        private final List<Long> boardIdList;

        @Builder
        private ByOffset(@NonNull Integer page,
                         @NonNull Integer pageSize,
                         String nickname,
                         String title,
                         String content,
                         List<Long> boardIdList) {
            this.pageable = PageRequest.of(page - 1, pageSize);
            this.nickname = nickname;
            this.title = title;
            this.content = content;
            this.boardIdList = boardIdList;
        }
    }
}
