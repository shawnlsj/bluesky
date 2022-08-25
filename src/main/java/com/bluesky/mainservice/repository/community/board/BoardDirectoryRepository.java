package com.bluesky.mainservice.repository.community.board;

import com.bluesky.mainservice.repository.community.board.domain.BoardDirectory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardDirectoryRepository extends JpaRepository<BoardDirectory, Integer>, BoardDirectoryRepositoryCustom {

}
