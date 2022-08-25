package com.bluesky.mainservice.repository.community.board.dto;

import com.bluesky.mainservice.repository.community.constant.CommunityType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class BoardTextContentDto {

    private Long id;
    private String title;
    private String content;
    private CommunityType communityType;
    private Integer boardDirectoryId;
    private String boardDirectoryName;
    private LocalDateTime createdDate;
    private UUID userId;
    private String profileImage;
    private String nickname;

    @QueryProjection
    public BoardTextContentDto(Long id,
                               String title,
                               String content,
                               CommunityType communityType,
                               Integer boardDirectoryId,
                               String boardDirectoryName,
                               LocalDateTime createdDate,
                               UUID userId,
                               String profileImage,
                               String nickname) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.communityType = communityType;
        this.boardDirectoryId = boardDirectoryId;
        this.boardDirectoryName = boardDirectoryName;
        this.createdDate = createdDate;
        this.userId = userId;
        this.profileImage = profileImage;
        this.nickname = nickname;
    }
}
