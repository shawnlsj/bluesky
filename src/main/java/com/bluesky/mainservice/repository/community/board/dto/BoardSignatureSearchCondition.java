package com.bluesky.mainservice.repository.community.board.dto;

import com.bluesky.mainservice.repository.community.board.constant.BoardState;
import com.bluesky.mainservice.repository.community.constant.CommunityType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class BoardSignatureSearchCondition {

    @Getter
    public static class BoardTitleNoUserDtoSearchCondition {

        private final Pageable pageable;
        private final CommunityType communityType;
        private final Integer boardDirectoryId;
        private final Integer minLikesCount;
        private final Integer minViewCount;
        private final Integer minReplyCount;
        private final LocalDateTime since;
        private final LocalDateTime until;

        private Boolean orderById;
        private Boolean orderByLikesCount;
        private Boolean orderByViewCount;
        private Boolean orderByReplyCount;

        @Builder
        private BoardTitleNoUserDtoSearchCondition(@NonNull Integer page,
                                                   @NonNull Integer pageSize,
                                                   CommunityType communityType,
                                                   Integer boardDirectoryId,
                                                   Integer minLikesCount,
                                                   Integer minViewCount,
                                                   Integer minReplyCount,
                                                   LocalDateTime since,
                                                   LocalDateTime until) {
            pageable = PageRequest.of(page - 1, pageSize);
            this.communityType = communityType;
            this.boardDirectoryId = boardDirectoryId;
            this.minLikesCount = minLikesCount;
            this.minViewCount = minViewCount;
            this.minReplyCount = minReplyCount;
            this.since = since;
            this.until = until;
            orderById = true;
        }

        public void orderByLikesCount() {
            resetOrderCondition();
            orderByLikesCount = true;
        }

        public void orderByViewCount() {
            resetOrderCondition();
            orderByViewCount = true;
        }

        public void orderByReplyCount() {
            resetOrderCondition();
            orderByReplyCount = true;
        }

        private void resetOrderCondition() {
            orderById = null;
            orderByLikesCount = null;
            orderByViewCount = null;
            orderByReplyCount = null;
        }
    }

    @Getter
    public static class BoardTitleDtoSearchCondition {

        private final Integer directoryId;
        private final Integer categoryId;
        private final Pageable pageable;
        private final Integer minLikesCount;
        private final Integer minViewCount;
        private final Integer minReplyCount;
        private final String title;
        private final String content;
        private final String nickname;
        private final BoardState boardState;
        private final List<Long> boardIdList;


        @Builder
        private BoardTitleDtoSearchCondition(Integer directoryId,
                                             Integer categoryId,
                                             @NonNull Integer page,
                                             @NonNull Integer pageSize,
                                             Integer minLikesCount,
                                             Integer minViewCount,
                                             Integer minReplyCount,
                                             String title,
                                             String content,
                                             String nickname,
                                             BoardState boardState,
                                             List<Long> boardIdList) {
            pageable = PageRequest.of(page - 1, pageSize);
            this.directoryId = directoryId;
            this.categoryId = categoryId;
            this.minLikesCount = minLikesCount;
            this.minViewCount = minViewCount;
            this.minReplyCount = minReplyCount;
            this.title = title;
            this.content = content;
            this.nickname = nickname;
            this.boardState = Objects.requireNonNullElse(boardState, BoardState.ACTIVE);
            this.boardIdList = boardIdList;
        }
    }
}
