package com.bluesky.mainservice.repository.community.board;

import com.bluesky.mainservice.repository.community.board.dto.*;
import org.springframework.data.domain.Page;

public interface BoardRepositoryCustom {

    Page<BoardTextContentDto> searchBoardTextContentDto(BoardSearchCondition.ByOffset searchCondition);

    Page<BoardTitleNoUserDto> searchBoardTitleNoUserDto(BoardSignatureSearchCondition.BoardTitleNoUserDtoSearchCondition searchCondition);

    Page<BoardTitleDto> searchBoardTitleDto(BoardSignatureSearchCondition.BoardTitleDtoSearchCondition searchCondition);

    int countActiveUserBoard(Long userId);

//    Page<BoardDto> findDtoPage(Pageable pageable);
//
//    List<BoardDto> findDtoList(long cursor, int size);
//
//    Long findLastId();
}
