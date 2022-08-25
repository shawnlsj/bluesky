package com.bluesky.mainservice.repository.community.board;

import com.bluesky.mainservice.repository.community.board.dto.NestedReply;
import com.bluesky.mainservice.repository.community.board.dto.ReplySearchCondition;
import com.bluesky.mainservice.repository.community.board.dto.ReplySearchResultDto;
import com.bluesky.mainservice.repository.community.board.dto.TopLevelReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReplyRepositoryCustom {

    Page<TopLevelReply> findTopLevelReplyPage(Pageable pageable, Long boardId);

    List<NestedReply> findNestedReply(List<Long> groupId);

    Page<ReplySearchResultDto> search(ReplySearchCondition.ByOffset searchCondition);

    int countBoardReply(Long boardId);

    int countActiveUserReply(Long userId);
}
