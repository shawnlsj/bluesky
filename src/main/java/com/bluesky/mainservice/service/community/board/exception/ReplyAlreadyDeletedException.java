package com.bluesky.mainservice.service.community.board.exception;

/**
 * 댓글이 이미 삭제된 상태여서 프로세스 진행이 불가능할 때 발생합니다.
 */
public class ReplyAlreadyDeletedException extends RuntimeException {

    public ReplyAlreadyDeletedException() {
        super("이미 삭제된 댓글입니다.");
    }

    public ReplyAlreadyDeletedException(String message) {
        super(message);
    }

    public ReplyAlreadyDeletedException(String message, Throwable cause) {
        super(message, cause);
    }
}
