package com.bluesky.mainservice.repository.community.board;

import com.bluesky.mainservice.repository.community.board.dto.BoardDirectoryDto;
import com.bluesky.mainservice.repository.community.board.dto.BoardDirectoryWithBoardCount;
import com.bluesky.mainservice.repository.community.board.dto.QBoardDirectoryDto;
import com.bluesky.mainservice.repository.community.board.dto.QBoardDirectoryWithBoardCount;
import com.bluesky.mainservice.repository.community.constant.CommunityType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.bluesky.mainservice.repository.community.board.domain.QBoard.board;
import static com.bluesky.mainservice.repository.community.board.domain.QBoardDirectory.boardDirectory;
import static com.bluesky.mainservice.repository.community.domain.QCommunity.community;

@RequiredArgsConstructor
public class BoardDirectoryRepositoryCustomImpl implements BoardDirectoryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BoardDirectoryDto> findDtoByCommunityType(CommunityType communityType) {
        return queryFactory
                .select(new QBoardDirectoryDto(boardDirectory.id, boardDirectory.name))
                .from(boardDirectory)
                .join(boardDirectory.community, community)
                .where(community.communityType.eq(communityType))
                .fetch();
    }

    @Override
    public List<BoardDirectoryWithBoardCount> countTodayBoard() {
        return queryFactory
                .select(new QBoardDirectoryWithBoardCount(
                        community.communityType,
                        boardDirectory.id,
                        boardDirectory.name,
                        board.id.count()))
                .from(boardDirectory)
                .join(boardDirectory.community, community)
                .leftJoin(board)
                .on(board.boardDirectory.id.eq(boardDirectory.id))
                .on(board.createdDate.goe(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)))
                .on(board.createdDate.lt(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1)))
                .groupBy(boardDirectory.id)
                .fetch();
    }
}
