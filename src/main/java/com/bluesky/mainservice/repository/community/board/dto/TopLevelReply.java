package com.bluesky.mainservice.repository.community.board.dto;

import com.bluesky.mainservice.repository.community.board.constant.ReplyState;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class TopLevelReply {

    private Long id;
    private Long groupId;
    private String rawContent;
    private Integer likesCount;
    private LocalDateTime createdDate;
    private ReplyState replyState;
    private UUID userId;
    private String nickname;
    private String profileImage;

    @QueryProjection
    public TopLevelReply(Long id,
                         Long groupId,
                         String rawContent,
                         Integer likesCount,
                         LocalDateTime createdDate,
                         ReplyState replyState,
                         UUID userId,
                         String nickname,
                         String profileImage) {
        this.id = id;
        this.groupId = groupId;
        this.rawContent = rawContent;
        this.likesCount = likesCount;
        this.createdDate = createdDate;
        this.replyState = replyState;
        this.userId = userId;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
