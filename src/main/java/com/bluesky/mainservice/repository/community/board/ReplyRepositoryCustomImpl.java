package com.bluesky.mainservice.repository.community.board;

import com.bluesky.mainservice.repository.community.board.constant.BoardState;
import com.bluesky.mainservice.repository.community.board.constant.ReplyState;
import com.bluesky.mainservice.repository.community.board.domain.QReply;
import com.bluesky.mainservice.repository.community.board.dto.*;
import com.bluesky.mainservice.repository.user.domain.QUser;
import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.bluesky.mainservice.repository.community.board.domain.QBoard.board;
import static com.bluesky.mainservice.repository.user.domain.QUser.user;

@RequiredArgsConstructor
public class ReplyRepositoryCustomImpl implements ReplyRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final QReply reply = QReply.reply1;
    private static final QReply parentReply = new QReply("parentReply");
    private static final QUser parentReplyUser = new QUser("parentReplyUser");

    @Override
    public Page<ReplySearchResultDto> search(ReplySearchCondition.ByOffset searchCondition) {
        Pageable pageable = searchCondition.getPageable();
        List<ReplySearchResultDto> content = queryFactory
                .select(new QReplySearchResultDto(
                        board.id,
                        reply.textContent,
                        reply.createdDate,
                        user.uuid,
                        user.profileImage,
                        user.nickname))
                .from(reply)
                .join(reply.board, board)
                .join(reply.user, user)
                .where(reply.replyState.eq(ReplyState.ACTIVE))
                .where(board.boardState.eq(BoardState.ACTIVE))
                .where(reply.textContent.contains(searchCondition.getKeyword()))
                .where(boardDirectoryIdEq(searchCondition.getBoardDirectoryId()))
                .where(boardCategoryIdEq(searchCondition.getBoardCategoryId()))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();


        //카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(reply.count())
                .from(reply)
                .join(reply.board, board)
                .where(reply.replyState.eq(ReplyState.ACTIVE))
                .where(board.boardState.eq(BoardState.ACTIVE))
                .where(reply.textContent.contains(searchCondition.getKeyword()))
                .where(boardDirectoryIdEq(searchCondition.getBoardDirectoryId()))
                .where(boardCategoryIdEq(searchCondition.getBoardCategoryId()));
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<TopLevelReply> findTopLevelReplyPage(Pageable pageable, Long boardId) {
        List<TopLevelReply> content = queryFactory.select(new QTopLevelReply(
                        reply.id,
                        reply.groupId,
                        reply.rawContent,
                        reply.likesCount,
                        reply.createdDate,
                        reply.replyState,
                        user.uuid,
                        user.nickname,
                        user.profileImage))
                .from(reply)
                .join(reply.user, user)
                .where(reply.board.id.eq(boardId))
                .where(reply.reply.id.eq(new NullExpression<>(Long.class)))
                .where(reply.replyState.eq(ReplyState.ACTIVE).or(reply.replyCount.gt(0)))
                .orderBy(reply.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(reply.count())
                .from(reply)
                .where(reply.board.id.eq(boardId))
                .where(reply.reply.id.eq(new NullExpression<>(Long.class)))
                .where(reply.replyState.eq(ReplyState.ACTIVE).or(reply.replyCount.gt(0)));
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<NestedReply> findNestedReply(List<Long> groupId) {
        return queryFactory.select(new QNestedReply(
                        reply.id,
                        reply.groupId,
                        reply.rawContent,
                        reply.likesCount,
                        reply.createdDate,
                        reply.replyState,
                        user.uuid,
                        user.nickname,
                        user.profileImage,
                        parentReplyUser.uuid,
                        parentReplyUser.nickname
                ))
                .from(reply)
                .join(reply.user, user)
                .join(reply.reply, parentReply)
                .join(parentReply.user, parentReplyUser)
                .where(reply.groupId.in(groupId).
                        and(reply.reply.isNotNull()))
                .where(reply.replyState.eq(ReplyState.ACTIVE)
                        .or(reply.replyCount.gt(0))).fetch();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int countBoardReply(Long boardId) {
        return queryFactory
                .select(reply.count())
                .from(reply)
                .where(reply.board.id.eq(boardId))
                .where(reply.replyState.eq(ReplyState.ACTIVE))
                .fetchOne()
                .intValue();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int countActiveUserReply(Long userId) {
        return queryFactory
                .select(reply.count())
                .from(reply)
                .where(reply.user.id.eq(userId))
                .where(reply.replyState.eq(ReplyState.ACTIVE))
                .fetchOne()
                .intValue();
    }

    private BooleanExpression boardDirectoryIdEq(Integer boardDirectoryIdCond) {
        return boardDirectoryIdCond == null ? null : board.boardDirectory.id.eq(boardDirectoryIdCond);
    }

    private BooleanExpression boardCategoryIdEq(Integer boardCategoryIdCond) {
        return boardCategoryIdCond == null ? null : board.boardCategory.id.eq(boardCategoryIdCond);
    }
}

