package com.bluesky.mainservice.service.community.board.exception;

/**
 * 게시글이 조회 되어야 하지만 조회된 결과가 없을 때 발생합니다.
 */
public class BoardNotFoundException extends RuntimeException {

    public BoardNotFoundException() {
        super("존재하지 않는 게시글입니다.");
    }

    public BoardNotFoundException(String message) {
        super(message);
    }

    public BoardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
