package com.bluesky.mainservice.repository.community.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class BoardDto {

    private long id;
    private int categoryId;
    private String category;
    private UUID userId;
    private String nickname;
    private String profileImage;
    private String title;
    private String content;
    private int viewCount;
    private int replyCount;
    private int likesCount;
    private LocalDateTime createdDate;

    @QueryProjection
    public BoardDto(Long id,
                    Integer categoryId,
                    String category,
                    UUID userId,
                    String nickname,
                    String profileImage,
                    String title,
                    String content,
                    Integer viewCount,
                    Integer replyCount,
                    Integer likesCount,
                    LocalDateTime createdDate) {
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
