package com.bluesky.mainservice.service.community.board.dto;

import com.bluesky.mainservice.repository.community.board.constant.ReplyState;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ReplyDto {

    private final Long id;
    private final Long groupId;
    private final String content;
    private final Integer likesCount;
    private final LocalDateTime createdDate;
    private final ReplyState replyState;
    private final UUID userId;
    private final String nickname;
    private final String profileImage;
    private final boolean isTopLevel;
    private final UUID parentReplyUserId;
    private final String parentReplyUserNickname;
    private boolean isLikesClicked = false;

    @Builder
    private ReplyDto(@NonNull Long id,
                     @NonNull Long groupId,
                     @NonNull String content,
                     @NonNull Integer likesCount,
                     @NonNull LocalDateTime createdDate,
                     @NonNull ReplyState replyState,
                     @NonNull UUID userId,
                     @NonNull String nickname,
                     @NonNull String profileImage,
                     @NonNull Boolean isTopLevel,
                     UUID parentReplyUserId,
                     String parentReplyUserNickname) {
        if (!isTopLevel) {
            Assert.notNull(parentReplyUserId, "하위 댓글은 부모 댓글의 userId 가 null 일 수 없습니다.");
            Assert.notNull(parentReplyUserNickname, "하위 댓글은 부모 댓글의 userNickname 가 null 일 수 없습니다.");
        }
        this.id = id;
        this.groupId = groupId;
        this.content = content;
        this.likesCount = likesCount;
        this.createdDate = createdDate;
        this.replyState = replyState;
        this.userId = userId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.isTopLevel = isTopLevel;
        this.parentReplyUserId = parentReplyUserId;
        this.parentReplyUserNickname = parentReplyUserNickname;
    }

    public void clickLikes() {
        isLikesClicked = true;
    }
}
