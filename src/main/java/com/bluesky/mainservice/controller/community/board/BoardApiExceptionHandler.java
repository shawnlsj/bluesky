package com.bluesky.mainservice.controller.community.board;

import com.bluesky.mainservice.controller.error.dto.Error;
import com.bluesky.mainservice.service.community.board.exception.BoardAlreadyDeletedException;
import com.bluesky.mainservice.service.community.board.exception.BoardNotFoundException;
import com.bluesky.mainservice.service.community.board.exception.ReplyAlreadyDeletedException;
import com.bluesky.mainservice.service.community.board.exception.ReplyNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = {BoardApiController.class, BoardFragmentViewController.class})
public class BoardApiExceptionHandler {

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(BoardNotFoundException.class)
    Error boardNotFoundExHandler(BoardNotFoundException e) {
        log.debug("게시글이 존재하지 않습니다.", e);
        return new Error("게시글이 존재하지 않습니다.");
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(BoardAlreadyDeletedException.class)
    Error boardAlreadyDeletedExHandler(BoardAlreadyDeletedException e) {
        log.debug("이미 삭제된 게시글입니다.", e);
        return new Error("이미 삭제된 게시글입니다.");
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(ReplyNotFoundException.class)
    Error replyNotFoundExHandler(ReplyNotFoundException e) {
        log.debug("댓글이 존재하지 않습니다.", e);
        return new Error("댓글이 존재하지 않습니다.");
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(ReplyAlreadyDeletedException.class)
    Error replyAlreadyDeletedExHandler(ReplyAlreadyDeletedException e) {
        log.debug("이미 삭제된 댓글입니다.", e);
        return new Error("이미 삭제된 댓글입니다.");
    }
}
