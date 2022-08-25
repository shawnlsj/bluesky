package com.bluesky.mainservice.repository.community.board.dto;

import com.bluesky.mainservice.repository.community.constant.CommunityType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class BoardDirectoryWithBoardCount {
    private CommunityType communityType;
    private Integer boardDirectoryId;
    private String boardDirectoryName;
    private Long boardCount;

    @QueryProjection
    public BoardDirectoryWithBoardCount(CommunityType communityType,
                                        Integer boardDirectoryId,
                                        String boardDirectoryName,
                                        Long boardCount) {
        this.communityType = communityType;
        this.boardDirectoryId = boardDirectoryId;
        this.boardDirectoryName = boardDirectoryName;
        this.boardCount = boardCount;
    }
}
