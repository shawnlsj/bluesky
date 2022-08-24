package com.bluesky.mainservice.repository.community.board.domain;

import com.bluesky.mainservice.repository.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@Table(name = "board_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_category_id")
    private Integer id;

    @NotNull
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_directory_id")
    private BoardDirectory boardDirectory;

    public BoardCategory(Integer id) {
        this.id = id;
    }
}
