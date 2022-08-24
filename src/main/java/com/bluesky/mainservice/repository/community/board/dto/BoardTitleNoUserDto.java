package com.bluesky.mainservice.repository.community.board.dto;


import com.bluesky.mainservice.repository.community.constant.CommunityType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardTitleNoUserDto {

    private long id;
    private CommunityType communityType;
    private int directoryId;
    private String directoryName;
    private int categoryId;
    private String categoryName;
    private String title;
    private int replyCount;
    private int likesCount;
    private int viewCount;
    private LocalDateTime createdDate;

    @QueryProjection
    public BoardTitleNoUserDto(Long id,
                               CommunityType communityType,
                               Integer directoryId,
                               String directoryName,
                               Integer categoryId,
                               String categoryName,
                               String title,
                               Integer replyCount,
                               Integer likesCount,
                               Integer viewCount,
                               LocalDateTime createdDate) {
        this.id = id;
        this.communityType = communityType;
        this.directoryId = directoryId;
        this.directoryName = directoryName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.title = title;
        this.replyCount = replyCount;
        this.likesCount = likesCount;
        this.viewCount = viewCount;
        this.createdDate = createdDate;
    }
}
