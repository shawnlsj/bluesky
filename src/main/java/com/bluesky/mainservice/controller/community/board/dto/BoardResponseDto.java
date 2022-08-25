package com.bluesky.mainservice.controller.community.board.dto;

import com.bluesky.mainservice.repository.community.constant.CommunityType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BoardResponseDto {

    @Getter
    @RequiredArgsConstructor
    public static class ToggleLikesResult {
        private final boolean isSuccess;
    }

    @Getter
    @RequiredArgsConstructor
    public static class SaveBoardResult {
        private final boolean isSuccess;
    }

    @Getter
    @RequiredArgsConstructor
    public static class ModifyBoardResult {
        private final boolean isSuccess;
    }

    @Getter
    @RequiredArgsConstructor
    public static class DeleteBoardResult {
        private final boolean isSuccess;
    }

    @Getter
    @RequiredArgsConstructor
    public static class SaveReplyResult {
        private final boolean isSuccess;
    }

    @Getter
    @RequiredArgsConstructor
    public static class ModifyReplyResult {
        private final boolean isSuccess;
    }

    @Getter
    @RequiredArgsConstructor
    public static class DeletedReplyResult {
        private final boolean isSuccess;
    }

    @Getter
    @RequiredArgsConstructor
    public static class BoardRanking{
        private final List<SimpleBoardSignature> likesCountRankingBoardList;
        private final List<SimpleBoardSignature> viewCountRankingBoardList;
    }

    @Getter
    public static class SimpleBoardSignature {
        private final CommunityType communityType;
        private final int boardDirectoryId;
        private final String boardDirectoryName;
        private final long id;
        private final String title;
        private final String replyCount;

        @Builder
        private SimpleBoardSignature(@NonNull String title,
                                     @NonNull CommunityType communityType,
                                     @NonNull Integer boardDirectoryId,
                                     @NonNull String boardDirectoryName,
                                     @NonNull Long id,
                                     @NonNull String replyCount) {
            this.title = title;
            this.communityType = communityType;
            this.boardDirectoryId = boardDirectoryId;
            this.boardDirectoryName = boardDirectoryName;
            this.id = id;
            this.replyCount = replyCount;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class BoardDirectoryNavigation {
        private final Map<Integer, String> idAndNameMap;
        private final Integer currentId;
    }

    @Getter
    public static class BoardCountStatus{
        private final CommunityType communityType;
        private final int boardDirectoryId;
        private final String boardDirectoryName;
        private final String boardCount;

        @Builder
        private BoardCountStatus(@NonNull CommunityType communityType,
                                 @NonNull Integer boardDirectoryId,
                                 @NonNull String boardDirectoryName,
                                 @NonNull String boardCount) {
            this.communityType = communityType;
            this.boardDirectoryId = boardDirectoryId;
            this.boardDirectoryName = boardDirectoryName;
            this.boardCount = boardCount;
        }
    }

    @Getter
    public static class BoardSearchResult {

        private final long id;
        private final String title;
        private final String content;
        private final CommunityType communityType;
        private final int boardDirectoryId;
        private final String boardDirectoryName;
        private final LocalDateTime createdDate;
        private final UUID userId;
        private final String profileImage;
        private final String nickname;

        @Builder
        private BoardSearchResult(@NonNull Long id,
                                  @NonNull String title,
                                  @NonNull String content,
                                  @NonNull CommunityType communityType,
                                  @NonNull Integer boardDirectoryId,
                                  @NonNull String boardDirectoryName,
                                  @NonNull LocalDateTime createdDate,
                                  @NonNull UUID userId,
                                  @NonNull String profileImage,
                                  @NonNull String nickname) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.communityType = communityType;
            this.boardDirectoryId = boardDirectoryId;
            this.boardDirectoryName = boardDirectoryName;
            this.createdDate = createdDate;
            this.userId = userId;
            this.profileImage = profileImage;
            this.nickname = nickname;
        }
    }

    @Getter
    public static class BoardOutsideView {

        private final long id;
        private final int categoryId;
        private final String categoryName;
        private final String title;
        private final UUID userId;
        private final String nickname;
        private final String profileImage;
        private final int replyCount;
        private final String likesCount;
        private final String viewCount;
        private final LocalDateTime createdDate;

        @Builder
        private BoardOutsideView(@NonNull Long id,
                                 @NonNull Integer categoryId,
                                 @NonNull String categoryName,
                                 @NonNull String title,
                                 @NonNull UUID userId,
                                 @NonNull String nickname,
                                 @NonNull String profileImage,
                                 @NonNull Integer replyCount,
                                 @NonNull Integer likesCount,
                                 @NonNull Integer viewCount,
                                 @NonNull LocalDateTime createdDate) {
            if (likesCount > 9999) {
                this.likesCount = likesCount / 1000 + "k";
            } else {
                this.likesCount = String.valueOf(likesCount);
            }

            if (viewCount > 9999) {
                this.viewCount = viewCount / 1000 + "k";
            } else {
                this.viewCount = String.valueOf(viewCount);
            }

            this.id = id;
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.title = title;
            this.userId = userId;
            this.nickname = nickname;
            this.profileImage = profileImage;
            this.replyCount = replyCount;
            this.createdDate = createdDate;
        }
    }

    @Getter
    public static class BoardInsideView {

        private final long id;
        private final String category;
        private final UUID userId;
        private final String nickname;
        private final String profileImage;
        private final String title;
        private final String content;
        private final int viewCount;
        private final int replyCount;
        private final int likesCount;
        private final LocalDateTime createdDate;
        private final boolean likesClicked;

        @Builder
        private BoardInsideView(@NonNull Long id,
                                @NonNull String category,
                                @NonNull UUID userId,
                                @NonNull String nickname,
                                @NonNull String profileImage,
                                @NonNull String title,
                                @NonNull String content,
                                @NonNull Integer replyCount,
                                @NonNull Integer likesCount,
                                @NonNull Integer viewCount,
                                @NonNull LocalDateTime createdDate,
                                @NonNull Boolean likesClicked) {
            this.id = id;
            this.category = category;
            this.userId = userId;
            this.nickname = nickname;
            this.profileImage = profileImage;
            this.title = title;
            this.content = content;
            this.replyCount = replyCount;
            this.likesCount = likesCount;
            this.viewCount = viewCount;
            this.createdDate = createdDate;
            this.likesClicked = likesClicked;
        }
    }

    @Getter
    public static class BoardSimpleOutsideView {

        private final long boardId;
        private final CommunityType communityType;
        private final int directoryId;
        private final String categoryName;
        private final String title;
        private final int replyCount;

        private BoardSimpleOutsideView(@NonNull Long boardId,
                                       @NonNull CommunityType communityType,
                                       @NonNull Integer directoryId,
                                       @NonNull String categoryName,
                                       @NonNull String title,
                                       @NonNull Integer replyCount) {
            this.boardId = boardId;
            this.communityType = communityType;
            this.directoryId = directoryId;
            this.categoryName = categoryName;
            this.title = title;
            this.replyCount = replyCount;
        }
    }

    @Getter
    public static class BoardModifyView {

        private final long id;
        private final int categoryId;
        private final String title;
        private final String content;

        @Builder
        private BoardModifyView(@NonNull Long id,
                                @NonNull Integer categoryId,
                                @NonNull String title,
                                @NonNull String content) {
            this.id = id;
            this.categoryId = categoryId;
            this.title = title;
            this.content = content;
        }
    }

    @Getter
    public static class ReplySearchResult {

        private final long boardId;
        private final String content;
        private final LocalDateTime createdDate;
        private final UUID userId;
        private final String profileImage;
        private final String nickname;

        @Builder
        private ReplySearchResult(@NonNull Long boardId,
                                  @NonNull String content,
                                  @NonNull LocalDateTime createdDate,
                                  @NonNull UUID userId,
                                  @NonNull String profileImage,
                                  @NonNull String nickname) {
            this.boardId = boardId;
            this.content = content;
            this.createdDate = createdDate;
            this.userId = userId;
            this.profileImage = profileImage;
            this.nickname = nickname;
        }
    }

    @Getter
    public static class ReplyInsideView {

        private final Long id;
        private final String content;
        private final Integer likesCount;
        private final LocalDateTime createdDate;
        private final UUID userId;
        private final String nickname;
        private final String profileImage;
        private final boolean isTopLevel;
        private final UUID parentReplyUserId;
        private final String parentReplyUserNickname;
        private final Boolean isLikesClicked;
        private final boolean isLastOfGroup;
        private final String deletedBy;

        @Builder
        private ReplyInsideView(@NonNull Long id,
                                String content,
                                Integer likesCount,
                                LocalDateTime createdDate,
                                @NonNull UUID userId,
                                @NonNull String nickname,
                                @NonNull String profileImage,
                                @NonNull Boolean isTopLevel,
                                UUID parentReplyUserId,
                                String parentReplyUserNickname,
                                Boolean isLikesClicked,
                                @NonNull Boolean isLastOfGroup,
                                @NonNull String deletedBy) {
            if (!isTopLevel && !StringUtils.hasText(deletedBy)) {
                Assert.notNull(parentReplyUserId, "삭제되지 않은 하위 댓글은 부모 댓글의 userId 가 null 일 수 없습니다.");
                Assert.notNull(parentReplyUserNickname, "삭제되지 않은 하위 댓글은 부모 댓글의 userNickname 가 null 일 수 없습니다.");
            }

            if (!StringUtils.hasText(deletedBy)) {
                Assert.notNull(content, "삭제되지 않은 댓글의 content 는 null 일 수 없습니다.");
                Assert.notNull(likesCount, "삭제되지 않은 댓글의 likesCount 는 null 일 수 없습니다.");
                Assert.notNull(createdDate, "삭제되지 않은 댓글의 createdDate 는 null 일 수 없습니다.");
                Assert.notNull(isLikesClicked, "삭제되지 않은 댓글의 isLikesClicked 는 null 일 수 없습니다.");
            }

            this.id = id;
            this.content = content;
            this.likesCount = likesCount;
            this.createdDate = createdDate;
            this.userId = userId;
            this.nickname = nickname;
            this.profileImage = profileImage;
            this.isTopLevel = isTopLevel;
            this.parentReplyUserId = parentReplyUserId;
            this.parentReplyUserNickname = parentReplyUserNickname;
            this.isLikesClicked = isLikesClicked;
            this.isLastOfGroup = isLastOfGroup;
            this.deletedBy = deletedBy;
        }
    }

    @Getter
    public static class PageNavigation {

        private final int startPageNum;
        private final int endPageNum;
        private final int totalPagesNum;
        private final int requestedPageNum;
        private final int pageNavigationSize;

        public PageNavigation(int page, int totalPagesNum, int pageNavigationSize) {
            this.pageNavigationSize = pageNavigationSize;
            this.totalPagesNum = totalPagesNum;
            requestedPageNum = page;

            //요청 페이지가 총 페이지 수보다 더 크면
            if (page > totalPagesNum) {
                startPageNum = page;
                endPageNum = page;
            } else {
                startPageNum = calculateStartPageNum();
                endPageNum = calculateEndPageNum();
            }
        }

        private int calculateStartPageNum() {
            //요청한 페이지 번호를 페이지네비게이션 크기로 나눈 몫을 이용하여 계산식을 만들 때,
            //나머지가 없는 경우는 페이지네비게이션 내의 다른 번호보다 몫이 1이 더 많으므로 분기를 나눈다
            if (requestedPageNum % pageNavigationSize == 0) {
                //예) 요청한 페이지 번호가 90번, 페이지네비게이션 크기가 10이라고 하면
                //시작 번호는 81번이 되야 하므로 (몫 - 1) * 10 을 한 뒤 1을 더해준다
                return ((requestedPageNum / pageNavigationSize) - 1) * pageNavigationSize + 1;
            } else {
                //예) 요청한 페이지 번호가 88번, 페이지 네이션 크기가 10이라고 하면
                //시작 번호는 81번이 되야 하므로 몫 * 10 을 한 뒤 1을 더해준다
                return (requestedPageNum / pageNavigationSize) * pageNavigationSize + 1;
            }
        }

        private int calculateEndPageNum() {
            //요청한 페이지 번호를 페이지네비게이션 크기로 나눈 몫을 이용하여 계산식을 만들 때,
            //나머지가 0인 경우는 페이지네비게이션 내의 왼쪽 번호들보다 몫이 1이 더 많으므로 분기를 나눈다
            if (requestedPageNum % pageNavigationSize == 0) {
                //요청한 페이지 번호를 페이지네비게이션 크기로 나눈 나머지가 0이라는 것은
                //요청한 페이지 번호가 페이지네이션의 끝 번호라는 것을 의마한다
                return requestedPageNum;
            } else {
                //예) 요청한 페이지 번호가 87, 페이지네비게이션 크기가 10이라고 하면
                //마지막 번호는 90이 되야 하므로 (몫 + 1) * 10 을 한다
                int endOfPageNavigationNum = ((requestedPageNum / pageNavigationSize) + 1) * pageNavigationSize;

                //총 페이지 수가 네비게이션의 마지막 번호보다 작은 경우는
                //총 페이지 수가 네비게이션의 마지막 번호가 된다
                return Math.min(totalPagesNum, endOfPageNavigationNum);
            }
        }
    }
}
