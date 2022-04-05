package com.bluesky.mainservice.repository;

import com.bluesky.mainservice.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
