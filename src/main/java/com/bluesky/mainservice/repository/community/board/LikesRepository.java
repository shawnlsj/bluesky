package com.bluesky.mainservice.repository.community.board;

import com.bluesky.mainservice.repository.community.board.domain.Likes;
import com.bluesky.mainservice.repository.community.board.dto.ReplyLikesDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    @Query("select (count(l) > 0) from Likes l " +
            "join l.user u " +
            "where l.board.id = :boardId " +
            "and u.uuid = :userId")
    boolean existsByBoardIdAndUserId(@Param("boardId") Long boardId, @Param("userId") UUID userId);

    boolean existsByBoardIdAndUserId(Long boardId, Long userId);

    boolean existsByReplyIdAndUserId(Long replyId, Long userId);

    @Modifying
    @Query("delete from Likes l where l.board.id = :boardId and l.user.id = :userId")
    void deleteByBoardIdAndUserId(@Param("boardId") Long boardId, @Param("userId") Long userId);

    @Modifying
    @Query("delete from Likes l where l.reply.id = :replyId and l.user.id = :userId")
    void deleteByReplyIdAndUserId(@Param("replyId") Long replyId, @Param("userId") Long userId);

    @Modifying
    @Query("delete from Likes l where l.board.id = :boardId")
    void deleteByBoardId(@Param("boardId") Long boardId);

    @Modifying
    @Query("delete from Likes l where l.reply.id = :replyId")
    void deleteByReplyId(@Param("replyId") Long replyId);

    @Query("select new com.bluesky.mainservice.repository.community.board.dto.ReplyLikesDto(l.reply.id) " +
            "from Likes l " +
            "where l.user.id = :userId " +
            "and l.reply.id in :replyIdList")
    List<ReplyLikesDto> findByReplyIdAndUserId(@Param("replyIdList") List<Long> replyIdList,
                                               @Param("userId") Long userId);
}
