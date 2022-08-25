package com.bluesky.mainservice.repository.community.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ReplySearchResultDto {

    private Long boardId;
    private String content;
    private LocalDateTime createdDate;
    private UUID userId;
    private String profileImage;
    private String nickname;

    @QueryProjection
    public ReplySearchResultDto(Long boardId,
                                String content,
                                LocalDateTime createdDate,
                                UUID userId,
                                String profileImage,
                                String nickname) {
        this.boardId = boardId;
        this.content = content;
        this.createdDate = createdDate;
        this.userId = userId;
        this.profileImage = profileImage;
        this.nickname = nickname;
    }
}
