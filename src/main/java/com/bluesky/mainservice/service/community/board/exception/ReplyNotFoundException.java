package com.bluesky.mainservice.service.community.board.exception;

/**
 * 댓글이 조회 되어야 하지만 조회된 결과가 없을 때 발생합니다.
 */
public class ReplyNotFoundException extends RuntimeException{

    public ReplyNotFoundException() {
        super("존재하지 않는 댓글입니다.");
    }

    public ReplyNotFoundException(String message) {
        super(message);
    }

    public ReplyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
