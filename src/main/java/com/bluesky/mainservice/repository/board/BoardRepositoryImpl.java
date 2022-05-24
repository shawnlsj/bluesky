package com.bluesky.mainservice.repository.board;

import static com.bluesky.mainservice.repository.domain.QBoard.*;
import com.bluesky.mainservice.repository.dto.BoardDto;
import com.bluesky.mainservice.repository.dto.QBoardDto;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    @Override
    public Page<BoardDto> findDtoPage(Pageable pageable) {
        List<BoardDto> content = queryFactory.select(new QBoardDto(board.id, board.createDateTime, board.title, board.content))
                .from(board)
                .orderBy(board.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = queryFactory.select(board.count())
                .from(board);
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
