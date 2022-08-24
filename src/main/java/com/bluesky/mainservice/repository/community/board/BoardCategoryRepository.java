package com.bluesky.mainservice.repository.community.board;

import com.bluesky.mainservice.repository.community.board.domain.BoardCategory;
import com.bluesky.mainservice.repository.community.board.dto.BoardCategoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardCategoryRepository extends JpaRepository<BoardCategory, Integer> {

    @Query("select new com.bluesky.mainservice.repository.community.board.dto.BoardCategoryDto(bc.id, bc.name) " +
            "from BoardCategory bc " +
            "where bc.boardDirectory.id = :boardDirectoryId")
    List<BoardCategoryDto> findByBoardDirectoryId(@Param("boardDirectoryId") Integer id);

}
