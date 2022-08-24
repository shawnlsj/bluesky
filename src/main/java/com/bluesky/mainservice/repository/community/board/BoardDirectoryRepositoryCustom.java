package com.bluesky.mainservice.repository.community.board;

import com.bluesky.mainservice.repository.community.board.dto.BoardDirectoryDto;
import com.bluesky.mainservice.repository.community.board.dto.BoardDirectoryWithBoardCount;
import com.bluesky.mainservice.repository.community.constant.CommunityType;

import java.util.List;

public interface BoardDirectoryRepositoryCustom {
    List<BoardDirectoryDto> findDtoByCommunityType(CommunityType communityType);
    List<BoardDirectoryWithBoardCount> countTodayBoard();
}
