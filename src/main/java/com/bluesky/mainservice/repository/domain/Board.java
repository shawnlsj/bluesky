package com.bluesky.mainservice.repository.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "BOARD_SEQ_GENERATOR",
        sequenceName = "BOARD_SEQ",
        initialValue = 1, allocationSize = 100
)
public class Board {

    @Builder
    public Board(LocalDateTime createDateTime, String title, String content) {
        this.createDateTime = createDateTime;
        this.title = title;
        this.content = content;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "BOARD_ID")
    private Long id;

    @Column(name = "CREATION_DATE")
    private LocalDateTime createDateTime;

    private String title;
    private String content;
}
