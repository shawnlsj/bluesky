package com.bluesky.mainservice.service.community.board.exception;

/**
 * 게시글이 이미 삭제된 상태여서 프로세스 진행이 불가능할 때 발생합니다.
 */
public class BoardAlreadyDeletedException extends RuntimeException{

    public BoardAlreadyDeletedException() {
        super("이미 삭제된 게시글입니다.");
    }

    public BoardAlreadyDeletedException(String message) {
        super(message);
    }

    public BoardAlreadyDeletedException(String message, Throwable cause) {
        super(message, cause);
    }
}
