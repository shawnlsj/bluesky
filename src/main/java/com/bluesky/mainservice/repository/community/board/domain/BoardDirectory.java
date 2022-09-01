package com.bluesky.mainservice.repository.community.board.domain;

import com.bluesky.mainservice.repository.BaseTimeEntity;
import com.bluesky.mainservice.repository.community.domain.Community;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "board_directory")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardDirectory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_directory_id")
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @OneToMany(mappedBy = "boardDirectory")
    private List<BoardCategory> boardCategoryList = new ArrayList<>();

    public BoardDirectory(Integer id) {
        this.id = id;
    }
}
