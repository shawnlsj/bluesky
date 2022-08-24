package com.bluesky.mainservice.service.community.board;

import com.bluesky.mainservice.repository.community.CommunityRepository;
import com.bluesky.mainservice.repository.community.board.BoardCategoryRepository;
import com.bluesky.mainservice.repository.community.board.BoardRepository;
import com.bluesky.mainservice.repository.community.board.LikesRepository;
import com.bluesky.mainservice.repository.community.board.ReplyRepository;
import com.bluesky.mainservice.repository.community.board.constant.BoardState;
import com.bluesky.mainservice.repository.community.board.constant.ReplyState;
import com.bluesky.mainservice.repository.community.board.domain.*;
import com.bluesky.mainservice.repository.community.board.dto.NestedReply;
import com.bluesky.mainservice.repository.community.board.dto.TopLevelReply;
import com.bluesky.mainservice.repository.community.board.dto.ReplyLikesDto;
import com.bluesky.mainservice.repository.community.domain.Community;
import com.bluesky.mainservice.repository.user.UserRepository;
import com.bluesky.mainservice.repository.user.domain.User;
import com.bluesky.mainservice.service.community.board.dto.*;
import com.bluesky.mainservice.service.community.board.exception.BoardAlreadyDeletedException;
import com.bluesky.mainservice.service.community.board.exception.BoardNotFoundException;
import com.bluesky.mainservice.service.community.board.exception.ReplyAlreadyDeletedException;
import com.bluesky.mainservice.service.community.board.exception.ReplyNotFoundException;
import com.bluesky.mainservice.service.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final LikesRepository likesRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityrepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final BoardCategoryRepository boardCategoryRepository;

    public void toggleBoardLikes(long boardId, UUID userId) {
        Board board = boardRepository
                .findByIdAndBoardState(boardId, BoardState.ACTIVE)
                .orElseThrow(BoardAlreadyDeletedException::new);

        User user = userRepository.findByUuid(userId)
                .orElseThrow(UserNotFoundException::new);

        //likes 를 누르지 않은 상태이면
        //likes 카운트를 1 올리고 likes 를 저장
        if (!likesRepository.existsByBoardIdAndUserId(boardId, userId)) {
            board.upLikesCount();
            Likes likes = new Likes(board, user);
            likesRepository.save(likes);
        } else {

            //likes 누른 상태이면
            //likes 카운트를 1 내리고 likes 를 삭제
            board.downLikesCount();
            likesRepository.deleteByBoardIdAndUserId(boardId, user.getId());
        }
    }

    public void toggleReplyLikes(long replyId, UUID userId) {
        Reply reply = replyRepository.findByIdAndReplyState(replyId, ReplyState.ACTIVE)
                .orElseThrow(ReplyAlreadyDeletedException::new);

        User user = userRepository.findByUuid(userId)
                .orElseThrow(UserNotFoundException::new);

        //게시글이 삭제된 상태이면 예외 발생
        if (reply.getBoard().isDeleted()) {
            throw new BoardAlreadyDeletedException();
        }

        //likes 를 누르지 않은 상태이면
        //likes 카운트를 1 올리고 likes 를 저장
        if (!likesRepository.existsByReplyIdAndUserId(replyId, user.getId())) {
            reply.upLikesCount();
            Likes likes = new Likes(reply, user);
            likesRepository.save(likes);
        } else {
            //likes 누른 상태이면
            //likes 카운트를 1 내리고 likes 를 삭제
            reply.downLikesCount();
            likesRepository.deleteByReplyIdAndUserId(replyId, user.getId());
        }
    }

    public Long saveBoard(NewBoard newBoard) {
        Integer communityId = communityrepository.findIdByCommunityType(newBoard.getCommunityType());
        Long userId = userRepository.findIdByUuid(newBoard.getUserId())
                .orElseThrow(UserNotFoundException::new);

        Board board = Board.builder()
                .title(newBoard.getTitle())
                .content(newBoard.getContent())
                .community(new Community(communityId))
                .boardDirectory(new BoardDirectory(newBoard.getDirectoryId()))
                .boardCategory(new BoardCategory(newBoard.getCategoryId()))
                .user(new User(userId))
                .build();
        boardRepository.save(board);
        log.info("게시글 저장 완료");
        return board.getId();
    }

    public void modifyBoard(ModifiedBoard modifiedBoard) {
        Long boardId = modifiedBoard.getId();
        Board board = boardRepository.findByIdAndUserUuid(boardId, modifiedBoard.getUserId())
                .orElseThrow(BoardNotFoundException::new);

        if (board.isDeleted()) {
            throw new BoardAlreadyDeletedException();
        }

        BoardCategory boardCategory = boardCategoryRepository.findById(modifiedBoard.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("BoardCategory 를 잧을 수 없습니다."));

        board.modifyTitle(modifiedBoard.getTitle());
        board.modifyContent(modifiedBoard.getContent());
        board.modifyCategory(boardCategory);

        log.info("게시글 수정 완료");
    }

    public void deleteBoard(Long boardId, UUID userId) {
        User user = userRepository.findByUuid(userId)
                .orElseThrow(UserNotFoundException::new);
        Board board = boardRepository.findByIdAndUserId(boardId, user.getId())
                .orElseThrow(BoardNotFoundException::new);

        //삭제된 게시글이면 예외 발생
        if (board.isDeleted()) {
            throw new BoardAlreadyDeletedException();
        }

        //좋아요 삭제
        likesRepository.deleteByBoardId(boardId);

        //게시글 삭제
        if (user.isAdmin()) {
            board.deletedByAdmin();
        } else {
            board.deletedByUser();
        }
        log.info("게시글 삭제 완료");
    }

    public Long saveReply(NewReply newReply) {
        Long boardId = newReply.getBoardId();
        Long parentReplyId = newReply.getReplyId();
        String content = newReply.getContent();

        //댓글 작성자의 pk 를 조회하여
        //연관관계 저장용 엔티티를 생성
        Long userId = userRepository.findIdByUuid(newReply.getUserId())
                .orElseThrow(UserNotFoundException::new);
        User user = new User(userId);

        //댓글을 등록할 게시글 조회하고 게시글의 댓글 수를 +1
        Board board = boardRepository.findByIdAndBoardState(boardId, BoardState.ACTIVE)
                .orElseThrow(BoardAlreadyDeletedException::new);
        board.upReplyCount();

        //부모 댓글의 id 가 없으면 최상위 댓글로 연관관계를 저장
        //아닌 경우에는 댓글의 답글로 연관관계를 저장
        Reply reply;
        if (parentReplyId == null) {
            reply = new Reply(content, board, user);

            //댓글을 저장하여 id를 부여하고
            //그룹 id 를 자신의 id 값으로 설정
            replyRepository.save(reply);
            reply.grantGroupId(reply.getId());
        } else {
            //부모 댓글을 조회하고 부모 댓글의 댓글 수를 +1
            Reply parentReply = replyRepository
                    .findByIdAndReplyState(parentReplyId, ReplyState.ACTIVE)
                    .orElseThrow(ReplyAlreadyDeletedException::new);
            reply = new Reply(content, board, parentReply, user);
            parentReply.upReplyCount();

            //부모가 최상위 댓글이 아니라면
            //부모가 속한 그룹의 최상위 댓글을 조회하여 댓글 수를 +1
            if (!parentReply.isTopLevel()) {
                Reply topLevelReply = replyRepository.findTopLevelReply(parentReply.getGroupId())
                        .orElseThrow(ReplyNotFoundException::new);
                topLevelReply.upReplyCount();
            }

            //그룹 id 를 부모의 그룹 id로 설정 후 저장
            reply.grantGroupId(parentReply.getGroupId());
            replyRepository.save(reply);
        }
        log.info("댓글 저장 완료");
        return reply.getId();
    }

    public void modifyReply(ModifiedReply modifiedReply) {
        Long replyId = modifiedReply.getId();
        Reply reply = replyRepository
                .find(replyId, modifiedReply.getUserId())
                .orElseThrow(ReplyNotFoundException::new);

        if (reply.isDeleted()) {
            throw new ReplyAlreadyDeletedException();
        }

        reply.modifyContent(modifiedReply.getContent());
        log.info("댓글 수정 완료");
    }

    public void deleteReply(Long replyId, UUID userId) {
        User user = userRepository.findByUuid(userId)
                .orElseThrow(UserNotFoundException::new);
        Reply reply = replyRepository.findWithParentReply(replyId, user.getId())
                .orElseThrow(ReplyNotFoundException::new);

        //삭제된 댓글이면 예외 발생
        if (reply.isDeleted()) {
            throw new ReplyAlreadyDeletedException();
        }

        //삭제된 게시글이면 예외 발생
        Board board = reply.getBoard();
        if (board.isDeleted()) {
            throw new BoardAlreadyDeletedException();
        }

        //좋아요 삭제
        likesRepository.deleteByReplyId(replyId);

        //댓글 삭제
        if (user.isAdmin()) {
            reply.deletedByAdmin();
        } else {
            reply.deletedByUser();
        }

        //자신이 최상위 댓글이 아닌 경우
        //부모의 댓글수를 -1
        if (!reply.isTopLevel()) {
            Reply parentReply = reply.getReply();
            parentReply.downReplyCount();

            //부모가 최상위 댓글이 아니라면
            //최상위 댓글을 조회하여 댓글 수를 -1
            if (!parentReply.isTopLevel()) {
                Reply topLevelReply = replyRepository.findTopLevelReply(reply.getGroupId())
                        .orElseThrow(ReplyNotFoundException::new);
                topLevelReply.downReplyCount();
            }
        }

        //게시글의 댓글 카운트 -1
        board.downReplyCount();
        log.info("댓글 삭제 완료");
    }

    public BoardContent findBoard(long boardId, boolean upViewCount) {
        Board board = boardRepository.findByIdAndBoardState(boardId, BoardState.ACTIVE)
                .orElseThrow(BoardNotFoundException::new);
        User user = board.getUser();
        if (upViewCount) {
            board.upViewCount();
        }
        return BoardContent.builder()
                .id(board.getId())
                .categoryId(board.getBoardCategory().getId())
                .category(board.getBoardCategory().getName())
                .title(board.getTitle())
                .content(board.getRawContent())
                .userId(user.getUuid())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .createdDate(board.getCreatedDate())
                .likesCount(board.getLikesCount())
                .replyCount(board.getReplyCount())
                .viewCount(board.getViewCount())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<ReplyDto> findReplyList(ReplyFindCondition condition) {
        List<ReplyDto> replyDtoList = new ArrayList<>();

        //최상위 댓글을 조회하여 리스트에 추가
        Page<TopLevelReply> page =
                replyRepository.findTopLevelReplyPage(condition.getPageable(), condition.getBoardId());
        List<TopLevelReply> topLevelReplyList = page.getContent();
        List<Long> groupIdList = new ArrayList<>();
        for (TopLevelReply reply : topLevelReplyList) {
            groupIdList.add(reply.getGroupId());
            replyDtoList.add(ReplyDto.builder()
                    .id(reply.getId())
                    .groupId(reply.getGroupId())
                    .content(reply.getRawContent())
                    .likesCount(reply.getLikesCount())
                    .createdDate(reply.getCreatedDate())
                    .replyState(reply.getReplyState())
                    .userId(reply.getUserId())
                    .profileImage(reply.getProfileImage())
                    .nickname(reply.getNickname())
                    .isTopLevel(true)
                    .build());
        }

        //하위 댓글을 조회하여 dto 리스트에 추가
        if (topLevelReplyList.size() > 0) {
            List<NestedReply> nestedReply = replyRepository.findNestedReply(groupIdList);
            for (NestedReply reply : nestedReply) {
                replyDtoList.add(ReplyDto.builder()
                        .id(reply.getId())
                        .groupId(reply.getGroupId())
                        .content(reply.getRawContent())
                        .likesCount(reply.getLikesCount())
                        .createdDate(reply.getCreatedDate())
                        .replyState(reply.getReplyState())
                        .userId(reply.getUserId())
                        .nickname(reply.getNickname())
                        .profileImage(reply.getProfileImage())
                        .isTopLevel(false)
                        .parentReplyUserId(reply.getParentReplyUserId())
                        .parentReplyUserNickname(reply.getParentReplyUserNickname())
                        .build());
            }
        }

        //사용자의 아이디가 검색 조건에 있는 경우
        //해당 사용자가 likes 를 눌렀는지 확인
        if (condition.getUserId() != null) {
            //사용자 pk 조회
            Long userId = userRepository.findIdByUuid(condition.getUserId())
                    .orElseThrow(UserNotFoundException::new);

            //likes 를 눌렀는지 조회할 댓글 id 들을 리스트에 추가하고,
            //조회한 결과를 dto 에 담기 위해 map 에 dto 를 추가
            List<Long> replyIdList = new ArrayList<>();
            Map<Long, ReplyDto> replyDtoMap = new HashMap<>();
            for (ReplyDto reply : replyDtoList) {
                replyIdList.add(reply.getId());
                replyDtoMap.put(reply.getId(), reply);
            }

            //likes 를 눌렀는지 조회
            List<ReplyLikesDto> likesList
                    = likesRepository.findByReplyIdAndUserId(replyIdList, userId);

            //like 를 눌렀다고 dto 에 전달
            for (ReplyLikesDto likes : likesList) {
                replyDtoMap.get(likes.getReplyId()).clickLikes();
            }
        }

        replyDtoList.sort(Comparator
                .comparingLong(ReplyDto::getGroupId)
                .thenComparingLong(ReplyDto::getId));

        return PageableExecutionUtils.getPage(replyDtoList, page.getPageable(), page::getTotalElements);
    }
}
