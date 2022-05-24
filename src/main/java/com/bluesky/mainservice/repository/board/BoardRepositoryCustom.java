package com.bluesky.mainservice.repository.board;

import com.bluesky.mainservice.repository.dto.BoardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {

    Page<BoardDto> findDtoPage (Pageable pageable);
}
