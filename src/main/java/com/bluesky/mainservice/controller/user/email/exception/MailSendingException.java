package com.bluesky.mainservice.controller.user.email.exception;

import com.bluesky.mainservice.controller.user.email.EmailSender;

/**
 * {@link EmailSender EmailSender}
 * 에서 메일을 발송하는 과정 중 발생합니다.
 */
public class MailSendingException extends RuntimeException {
    public MailSendingException(Exception cause) {
        super(cause);
    }
}
