package com.bluesky.mainservice.repository.community.board;

import com.bluesky.mainservice.repository.community.board.constant.BoardState;
import com.bluesky.mainservice.repository.community.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    Optional<Board> findByIdAndBoardState(Long id, BoardState boardState);

    boolean existsByIdAndBoardState(Long id, BoardState boardState);

    @Query("select u.uuid from Board b " +
            "join b.user u " +
            "where b.id = :boardId " +
            "and b.boardState = :boardState")
    Optional<UUID> findUserUuidById(@Param("boardId") Long boardId, @Param("boardState") BoardState boardState);

    @Query("select b from Board b " +
            "where b.id = :id " +
            "and b.user.id = :userId")
    Optional<Board> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("select b from Board b " +
            "join b.user u " +
            "where b.id = :id " +
            "and u.uuid = :userId")
    Optional<Board> findByIdAndUserUuid(@Param("id") Long id, @Param("userId") UUID userId);
}
