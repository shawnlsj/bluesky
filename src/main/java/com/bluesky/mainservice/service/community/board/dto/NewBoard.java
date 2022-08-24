package com.bluesky.mainservice.service.community.board.dto;

import com.bluesky.mainservice.repository.community.constant.CommunityType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;

@Getter
public class NewBoard {

    private final String title;
    private final String content;
    private final UUID userId;
    private final CommunityType communityType;
    private final Integer directoryId;
    private final Integer categoryId;

    @Builder
    private NewBoard(@NonNull String title,
                     @NonNull String content,
                     @NonNull UUID userId,
                     @NonNull CommunityType communityType,
                     @NonNull Integer directoryId,
                     @NonNull Integer categoryId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.communityType = communityType;
        this.directoryId = directoryId;
        this.categoryId = categoryId;
    }
}
