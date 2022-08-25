package com.bluesky.mainservice.repository.community.board.domain;

import com.bluesky.mainservice.repository.BaseTimeEntity;
import com.bluesky.mainservice.repository.community.domain.Community;
import com.bluesky.mainservice.repository.community.board.constant.BoardState;
import com.bluesky.mainservice.repository.user.domain.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@Table(name = "board")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @NotNull
    private String title;

    @NotNull
    @Column(name = "row_content", length = 7_000)
    private String rawContent;

    @NotNull
    @Column(name = "text_content", length = 7_000)
    private String textContent;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "reply_count")
    private Integer replyCount;

    @Column(name = "likes_count")
    private Integer likesCount;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_directory_id")
    private BoardDirectory boardDirectory;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_category_id")
    private BoardCategory boardCategory;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "board_state")
    private BoardState boardState;

    public Board(Long id) {
        this.id = id;
    }

    @Builder
    private Board(@NonNull String title,
                  @NonNull String content,
                  @NonNull User user,
                  @NonNull Community community,
                  @NonNull BoardDirectory boardDirectory,
                  @NonNull BoardCategory boardCategory) {
        this.title = title;
        this.rawContent = content;
        this.textContent = convertToTextContent(rawContent);
        this.user = user;
        this.community = community;
        this.boardDirectory = boardDirectory;
        this.boardCategory = boardCategory;
        boardState = BoardState.ACTIVE;
        viewCount = 0;
        replyCount = 0;
        likesCount = 0;
    }

    public void upViewCount() {
        viewCount++;
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

    public void deletedByAdmin() {
        boardState = BoardState.DELETED_BY_ADMIN;
        likesCount = 0;
    }

    public void deletedByUser() {
        boardState = BoardState.DELETED_BY_USER;
        likesCount = 0;
    }

    public boolean isActive() {
        return boardState == BoardState.ACTIVE;
    }

    public boolean isDeleted() {
        return !isActive();
    }

    private String convertToTextContent(String rawContent) {
        //1. 줄바꿈 관련 태그를 모두 공백으로 치환
        //2. 태그를 제거
        //3. 이스케이핑 된 문자를 원래 문자로 치환
        //4. 연속 공백은 공백 하나로 치환
        //5. 마지막 공백은 제거
        return rawContent
                .replaceAll("(</div>|</p>|<br>)", " ")
                .replaceAll("(<.+?>)", "")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replaceAll("\\s{2,}", " ")
                .replaceAll("\\s$", "");
    }

    public void modifyCategory(BoardCategory category) {
        boardCategory = category;
    }

    public void modifyTitle(String title) {
        this.title = title;
    }

    public void modifyContent(String content) {
        this.rawContent = content;
        textContent = convertToTextContent(rawContent);
    }
}
