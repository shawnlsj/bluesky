package com.bluesky.mainservice.repository.community.board.domain;

import com.bluesky.mainservice.repository.BaseTimeEntity;
import com.bluesky.mainservice.repository.community.board.constant.ReplyState;
import com.bluesky.mainservice.repository.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Slf4j
@Getter
@Entity
@Table(name = "reply")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    @Column(name = "group_id")
    private Long groupId;

    @NotNull
    @Column(name = "row_content", length = 7_000)
    private String rawContent;

    @NotNull
    @Column(name = "text_content", length = 7_000)
    private String textContent;

    @NotNull
    @Column(name = "reply_count")
    private Integer replyCount;

    @NotNull
    @Column(name = "likes_count")
    private Integer likesCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_reply_id")
    private Reply reply;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "reply_state")
    private ReplyState replyState;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Reply(Long id) {
        this.id = id;
    }

    public Reply(String content, Board board, User user) {
        this.rawContent = content;
        this.textContent = convertToTextContent(rawContent);
        this.board = board;
        this.user = user;
        this.replyState = ReplyState.ACTIVE;
        replyCount = 0;
        likesCount = 0;
    }

    public Reply(String content, Board board, Reply reply, User user) {
        this.rawContent = content;
        this.textContent = convertToTextContent(rawContent);
        this.board = board;
        this.reply = reply;
        this.user = user;
        this.replyState = ReplyState.ACTIVE;
        replyCount = 0;
        likesCount = 0;
    }

    public boolean isTopLevel() {
        return id.equals(groupId);
    }

    public void grantGroupId(long groupId) {
        this.groupId = groupId;
    }

    public void upReplyCount() {
        replyCount++;
    }

    public void downReplyCount() {
        replyCount--;
    }

    public void upLikesCount() {
        likesCount++;
    }

    public void downLikesCount() {
        likesCount--;
    }

    private String convertToTextContent(String rawContent) {
        //1. 줄바꿈 태그를 공백으로 치환
        //2. 연속 공백은 공백 하나로 치환
        return rawContent
                .replace("<br>"," ")
                .replaceAll("\\s{2,}"," ");
    }

    public void modifyContent(String content) {
        this.rawContent = content;
        convertToTextContent(rawContent);
    }

    public void deletedByUser() {
        this.replyState = ReplyState.DELETED_BY_USER;
        likesCount = 0;
    }

    public void deletedByAdmin() {
        this.replyState = ReplyState.DELETED_BY_ADMIN;
        likesCount = 0;
    }

    public boolean isActive() {
        return replyState == ReplyState.ACTIVE;
    }

    public boolean isDeleted() {
        return !isActive();
    }
}
