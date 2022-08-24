package com.bluesky.mainservice.controller.community.board;

import com.bluesky.mainservice.controller.argument.LoginUser;
import com.bluesky.mainservice.controller.community.board.dto.BoardSaveForm;
import com.bluesky.mainservice.controller.community.board.dto.BoardUpdateForm;
import com.bluesky.mainservice.controller.community.board.dto.ReplySaveForm;
import com.bluesky.mainservice.controller.community.board.dto.ReplyUpdateForm;
import com.bluesky.mainservice.service.community.board.BoardService;
import com.bluesky.mainservice.service.community.board.dto.ModifiedBoard;
import com.bluesky.mainservice.service.community.board.dto.ModifiedReply;
import com.bluesky.mainservice.service.community.board.dto.NewBoard;
import com.bluesky.mainservice.service.community.board.dto.NewReply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.bluesky.mainservice.controller.community.board.dto.BoardResponseDto.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BoardApiController {

    private final BoardService boardService;

    @PostMapping("/board")
    public SaveBoardResult saveBoard(@Valid BoardSaveForm boardSaveForm, LoginUser loginUser) {
        NewBoard newBoard = NewBoard.builder()
                .communityType(boardSaveForm.getCommunityType())
                .directoryId(boardSaveForm.getDirectoryId())
                .categoryId(boardSaveForm.getCategoryId())
                .title(boardSaveForm.getTitle())
                .content(boardSaveForm.getContent())
                .userId(loginUser.getId()).build();
        boardService.saveBoard(newBoard);
        return new SaveBoardResult(true);
    }

    @PutMapping("/board/{boardId:[0-9]+}")
    public ModifyBoardResult modifyBoard(@Valid BoardUpdateForm boardUpdateForm,
                                         @PathVariable Long boardId,
                                         LoginUser loginUser) {
        ModifiedBoard modifiedBoard = ModifiedBoard.builder()
                .id(boardId)
                .categoryId(boardUpdateForm.getCategoryId())
                .title(boardUpdateForm.getTitle())
                .content(boardUpdateForm.getContent())
                .userId(loginUser.getId())
                .build();
        boardService.modifyBoard(modifiedBoard);
        return new ModifyBoardResult(true);
    }

    @DeleteMapping("/board/{boardId:[0-9]+}")
    public DeleteBoardResult deleteBoard(@PathVariable Long boardId, LoginUser loginUser) {
        boardService.deleteBoard(boardId, loginUser.getId());
        return new DeleteBoardResult(true);
    }

    @PostMapping("/reply")
    public SaveReplyResult saveReply(@Valid ReplySaveForm form, LoginUser loginUser) {
        NewReply newReply = NewReply.builder()
                .userId(loginUser.getId())
                .boardId(form.getBoardId())
                .replyId(form.getReplyId())
                .content(form.getContent())
                .build();
        boardService.saveReply(newReply);
        return new SaveReplyResult(true);
    }

    @PutMapping("/reply/{replyId:[0-9]+}")
    public ModifyReplyResult modifyReply(@Valid ReplyUpdateForm form,
                                         @PathVariable Long replyId,
                                         LoginUser loginUser) {
        ModifiedReply modifiedReply =
                new ModifiedReply(replyId, form.getContent(), loginUser.getId());
        boardService.modifyReply(modifiedReply);
        return new ModifyReplyResult(true);
    }

    @DeleteMapping("/reply/{replyId:[0-9]+}")
    public DeletedReplyResult deleteReply(@PathVariable Long replyId, LoginUser loginUser) {
        boardService.deleteReply(replyId, loginUser.getId());
        return new DeletedReplyResult(true);
    }

    @GetMapping("/board/toggle-likes")
    public ToggleLikesResult toggleBoardLikes(@RequestParam Long boardId, LoginUser loginUser) {
        boardService.toggleBoardLikes(boardId, loginUser.getId());
        return new ToggleLikesResult(true);
    }

    @GetMapping("/reply/toggle-likes")
    public ToggleLikesResult toggleReplyLikes(@RequestParam Long replyId, LoginUser loginUser) {
        boardService.toggleReplyLikes(replyId, loginUser.getId());
        return new ToggleLikesResult(true);
    }
}
