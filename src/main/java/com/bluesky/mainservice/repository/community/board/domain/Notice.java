package com.bluesky.mainservice.repository.community.board.domain;

import com.bluesky.mainservice.repository.BaseTimeEntity;
import com.bluesky.mainservice.repository.community.board.constant.NoticeScope;
import com.bluesky.mainservice.repository.community.board.constant.NoticeState;
import com.bluesky.mainservice.repository.community.domain.Community;
import com.bluesky.mainservice.repository.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@Table(name = "notice")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @NotNull
    private String title;

    @NotNull
    @Column(length = 10_000)
    private String content;

    @Column(name = "view_count")
    private Integer viewCount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "notice_state")
    private NoticeState noticeState;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "notice_scope")
    private NoticeScope noticeScope;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_directory_id")
    private BoardDirectory boardDirectory;
}
