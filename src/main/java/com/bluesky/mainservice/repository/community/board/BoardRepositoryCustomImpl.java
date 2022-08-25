package com.bluesky.mainservice.repository.community.board;

import com.bluesky.mainservice.repository.community.board.constant.BoardState;
import com.bluesky.mainservice.repository.community.board.dto.*;
import com.bluesky.mainservice.repository.community.constant.CommunityType;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.bluesky.mainservice.repository.community.board.domain.QBoard.board;
import static com.bluesky.mainservice.repository.community.board.domain.QBoardCategory.boardCategory;
import static com.bluesky.mainservice.repository.community.board.domain.QBoardDirectory.boardDirectory;
import static com.bluesky.mainservice.repository.community.domain.QCommunity.community;
import static com.bluesky.mainservice.repository.querydsl.QuerydslUtils.orBuilder;
import static com.bluesky.mainservice.repository.querydsl.QuerydslUtils.orderByBuilder;
import static com.bluesky.mainservice.repository.user.domain.QUser.user;
import static com.querydsl.core.types.dsl.Expressions.asNumber;

@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BoardTitleNoUserDto> searchBoardTitleNoUserDto(BoardSignatureSearchCondition.BoardTitleNoUserDtoSearchCondition searchCondition) {
        Pageable pageable = searchCondition.getPageable();

        List<BoardTitleNoUserDto> content =
                queryFactory.select(new QBoardTitleNoUserDto(
                                board.id,
                                community.communityType,
                                boardDirectory.id,
                                boardDirectory.name,
                                boardCategory.id,
                                boardCategory.name,
                                board.title,
                                board.replyCount,
                                board.likesCount,
                                board.viewCount,
                                board.createdDate))
                        .from(board)
                        .join(board.community, community)
                        .join(board.boardDirectory, boardDirectory)
                        .join(board.boardCategory, boardCategory)
                        .where(communityTypeEq(searchCondition.getCommunityType()),
                                directoryIdEq(searchCondition.getBoardDirectoryId()),
                                replyCountGoe(searchCondition.getMinReplyCount()),
                                likesCountGoe(searchCondition.getMinLikesCount()),
                                viewCountGoe(searchCondition.getMinViewCount()),
                                dateGoe(searchCondition.getSince()),
                                dateLt(searchCondition.getUntil()))
                        .orderBy(orderByBuilder(
                                idDesc(searchCondition.getOrderById()),
                                likesCountDesc(searchCondition.getOrderByLikesCount()),
                                viewCountDesc(searchCondition.getOrderByViewCount()),
                                replyCountDesc(searchCondition.getOrderByReplyCount())))
                        .limit(pageable.getPageSize())
                        .offset(pageable.getOffset())
                        .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(board.count())
                .from(board)
                .where(communityTypeEq(searchCondition.getCommunityType()),
                        replyCountGoe(searchCondition.getMinReplyCount()),
                        likesCountGoe(searchCondition.getMinLikesCount()),
                        viewCountGoe(searchCondition.getMinViewCount()));
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<BoardTitleDto> searchBoardTitleDto(BoardSignatureSearchCondition.BoardTitleDtoSearchCondition searchCondition) {
        Pageable pageable = searchCondition.getPageable();

        List<BoardTitleDto> content =
                queryFactory.select(new QBoardTitleDto(
                                board.id,
                                categoryId(searchCondition.getCategoryId()),
                                boardCategory.name,
                                board.title,
                                user.uuid,
                                user.nickname,
                                user.profileImage,
                                board.replyCount,
                                board.likesCount,
                                board.viewCount,
                                board.createdDate))
                        .from(board)
                        .join(board.user, user)
                        .join(board.community, community)
                        .join(board.boardCategory, boardCategory)
                        .where(boardStateEq(searchCondition.getBoardState()),
                                directoryIdEq(searchCondition.getDirectoryId()),
                                categoryIdEq(searchCondition.getCategoryId()),
                                replyCountGoe(searchCondition.getMinReplyCount()),
                                likesCountGoe(searchCondition.getMinLikesCount()),
                                viewCountGoe(searchCondition.getMinViewCount()))
                        .where(orBuilder(titleContains(searchCondition.getTitle()),
                                contentContains(searchCondition.getContent()),
                                nicknameContains(searchCondition.getNickname())))
                        .where(boardIdIn(searchCondition.getBoardIdList()))
                        .orderBy(board.id.desc())
                        .limit(pageable.getPageSize())
                        .offset(pageable.getOffset())
                        .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(board.count())
                .from(board)
                .where(boardStateEq(searchCondition.getBoardState()),
                        directoryIdEq(searchCondition.getDirectoryId()),
                        categoryIdEq(searchCondition.getCategoryId()),
                        replyCountGoe(searchCondition.getMinReplyCount()),
                        likesCountGoe(searchCondition.getMinLikesCount()),
                        viewCountGoe(searchCondition.getMinViewCount()))
                .where(orBuilder(titleContains(searchCondition.getTitle()),
                        contentContains(searchCondition.getContent()),
                        nicknameContains(searchCondition.getNickname())))
                .where(boardIdIn(searchCondition.getBoardIdList()));
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<BoardTextContentDto> searchBoardTextContentDto(BoardSearchCondition.ByOffset searchCondition) {
        Pageable pageable = searchCondition.getPageable();

        List<BoardTextContentDto> content =
                queryFactory.select(new QBoardTextContentDto(
                                board.id,
                                board.title,
                                board.textContent,
                                community.communityType,
                                boardDirectory.id,
                                boardDirectory.name,
                                board.createdDate,
                                user.uuid,
                                user.profileImage,
                                user.nickname))
                        .from(board)
                        .join(board.community, community)
                        .join(board.boardDirectory, boardDirectory)
                        .join(board.user, user)
                        .where(board.boardState.eq(BoardState.ACTIVE))
                        .where(orBuilder(titleContains(searchCondition.getTitle()),
                                contentContains(searchCondition.getContent()),
                                nicknameContains(searchCondition.getNickname())))
                        .where(boardIdIn(searchCondition.getBoardIdList()))
                        .orderBy(board.id.desc())
                        .limit(pageable.getPageSize())
                        .offset(pageable.getOffset())
                        .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(board.count())
                .from(board)
                .where(board.boardState.eq(BoardState.ACTIVE))
                .where(orBuilder(titleContains(searchCondition.getTitle()),
                        contentContains(searchCondition.getContent()),
                        nicknameContains(searchCondition.getNickname())))
                .where(boardIdIn(searchCondition.getBoardIdList()));
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int countActiveUserBoard(Long userId) {
        return queryFactory
                .select(board.count())
                .from(board)
                .where(board.user.id.eq(userId))
                .where(board.boardState.eq(BoardState.ACTIVE))
                .fetchOne()
                .intValue();
    }

    private Expression<Integer> categoryId(Integer categoryId) {
        return categoryId == null ? boardCategory.id : asNumber(categoryId).as("categoryId");
    }

    private BooleanExpression communityTypeEq(CommunityType communityTypeCond) {
        return communityTypeCond == null ? null : community.communityType.eq(communityTypeCond);
    }

    private BooleanExpression boardIdLt(Long boardIdCond) {
        return boardIdCond == null ? null : board.id.lt(boardIdCond);
    }

    private BooleanExpression directoryIdEq(Integer directoryIdCond) {
        return directoryIdCond == null ? null : board.boardDirectory.id.eq(directoryIdCond);
    }

    private BooleanExpression categoryIdEq(Integer categoryIdCond) {
        return categoryIdCond == null ? null : board.boardCategory.id.eq(categoryIdCond);
    }

    private BooleanExpression nicknameContains(String nicknameCond) {
        return nicknameCond == null ? null : user.nickname.contains((nicknameCond));
    }

    private BooleanExpression titleContains(String titleCond) {
        return titleCond == null ? null : board.title.contains(titleCond);
    }

    private BooleanExpression contentContains(String contentCond) {
        return contentCond == null ? null : board.textContent.contains(contentCond);
    }

    private BooleanExpression boardStateEq(BoardState boardState) {
        return boardState == null ? null : board.boardState.eq(boardState);
    }

    private BooleanExpression likesCountGoe(Integer likesCountCond) {
        return likesCountCond == null ? null : board.likesCount.goe(likesCountCond);
    }

    private BooleanExpression replyCountGoe(Integer replyCountCond) {
        return replyCountCond == null ? null : board.replyCount.goe(replyCountCond);
    }

    private BooleanExpression viewCountGoe(Integer viewCountCond) {
        return viewCountCond == null ? null : board.viewCount.goe(viewCountCond);
    }

    private BooleanExpression boardIdIn(List<Long> boardIdCond) {
        return boardIdCond == null ? null : board.id.in(boardIdCond);
    }

    private BooleanExpression dateGoe(LocalDateTime dateCond) {
        return dateCond == null ? null : board.createdDate.goe(dateCond);
    }

    private BooleanExpression dateLt(LocalDateTime dateCond) {
        return dateCond == null ? null : board.createdDate.lt(dateCond);
    }

    private OrderSpecifier<Long> idDesc(Boolean orderCond) {
        return orderCond == null ? null : board.id.desc();
    }

    private OrderSpecifier<Integer> likesCountDesc(Boolean orderCond) {
        return orderCond == null ? null : board.likesCount.desc();
    }

    private OrderSpecifier<Integer> viewCountDesc(Boolean orderCond) {
        return orderCond == null ? null : board.viewCount.desc();
    }

    private OrderSpecifier<Integer> replyCountDesc(Boolean orderCond) {
        return orderCond == null ? null : board.replyCount.desc();
    }
}
