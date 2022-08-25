package com.bluesky.mainservice.repository.community.board;

import com.bluesky.mainservice.repository.community.board.constant.ReplyState;
import com.bluesky.mainservice.repository.community.board.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReplyRepository extends JpaRepository<Reply, Long>, ReplyRepositoryCustom{
    Optional<Reply> findByIdAndReplyState(Long id, ReplyState replyState);

    boolean existsByIdAndReplyState(Long id, ReplyState replyState);

    @Query("select r from Reply r " +
            "join r.user u " +
            "where r.id = :id " +
            "and u.uuid = :userId")
    Optional<Reply> find(@Param("id") Long id,
                   @Param("userId") UUID userId);

    @Query("select r from Reply r " +
            "left join fetch r.reply " +
            "where r.id = :id " +
            "and r.user.id = :userId")
    Optional<Reply> findWithParentReply(@Param("id") Long id,
                         @Param("userId") Long userId);

    @Query("select r from Reply r " +
            "where r.id = r.groupId " +
            "and r.groupId = :groupId")
    Optional<Reply> findTopLevelReply(@Param("groupId") Long groupId);
}
