package com.bluesky.mainservice.repository.board;

import com.bluesky.mainservice.repository.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

}
