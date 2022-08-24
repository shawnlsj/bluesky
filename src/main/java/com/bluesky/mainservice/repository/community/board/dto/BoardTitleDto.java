package com.bluesky.mainservice.repository.community.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class BoardTitleDto {

    private long id;
    private int categoryId;
    private String categoryName;
    private String title;
    private UUID userId;
    private String nickname;
    private String profileImage;
    private int replyCount;
    private int likesCount;
    private int viewCount;
    private LocalDateTime createdDate;

    @QueryProjection
    public BoardTitleDto(Long id,
                         Integer categoryId,
                         String categoryName,
                         String title,
                         UUID userId,
                         String nickname,
                         String profileImage,
                         Integer replyCount,
                         Integer likesCount,
                         Integer viewCount,
                         LocalDateTime createdDate) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.title = title;
        this.userId = userId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.replyCount = replyCount;
        this.likesCount = likesCount;
        this.viewCount = viewCount;
        this.createdDate = createdDate;
    }
}
