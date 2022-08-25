package com.bluesky.mainservice.service.community.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class BoardContent {

    private final long id;
    private final int categoryId;
    private final String category;
    private final UUID userId;
    private final String nickname;
    private final String profileImage;
    private final String title;
    private final String content;
    private final int viewCount;
    private final int replyCount;
    private final int likesCount;
    private final LocalDateTime createdDate;

    @Builder
    private BoardContent(@NonNull Long id,
                         @NonNull Integer categoryId,
                         @NonNull String category,
                         @NonNull UUID userId,
                         @NonNull String nickname,
                         @NonNull String profileImage,
                         @NonNull String title,
                         @NonNull String content,
                         @NonNull Integer viewCount,
                         @NonNull Integer replyCount,
                         @NonNull Integer likesCount,
                         @NonNull LocalDateTime createdDate) {
        this.id = id;
        this.categoryId = categoryId;
        this.category = category;
        this.userId = userId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.replyCount = replyCount;
        this.likesCount = likesCount;
        this.createdDate = createdDate;
    }
}
