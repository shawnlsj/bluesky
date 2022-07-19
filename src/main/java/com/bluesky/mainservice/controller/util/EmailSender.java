package com.bluesky.mainservice.controller.util;

import com.bluesky.mainservice.config.security.jwt.JwtGenerator;
import com.bluesky.mainservice.controller.util.exception.MailSendingException;
import com.bluesky.mainservice.repository.user.constant.AccountType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;

import static org.apache.commons.codec.CharEncoding.*;
import static org.springframework.mail.javamail.MimeMessageHelper.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;


    @Async("sendMailExecutor")
    public void sendAuthenticationMail(String targetEmail, String destinationUrl, String token) {

        try {
            //템플릿 엔진에 넘겨줄 변수 세팅
            Context context = new Context();
            context.setVariable("destinationUrl", destinationUrl);
            context.setVariable("token", token);

            //HTML body 생성
            String html = templateEngine.process("user/join_email", context);

            //message 준비
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, MULTIPART_MODE_NO, UTF_8);
            messageHelper.setSubject("bluesky 메일인증 안내");
            messageHelper.setFrom("shawnlsj09@gmail.com","bluesky");
            messageHelper.setTo(targetEmail);
            messageHelper.setText(html, true);

            //메일 전송
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("create mail failure", e);
            throw new MailSendingException(e);
        } catch (MailException e) {
            log.error("send mail failure", e);
            throw new MailSendingException(e);
        }
    }

    @Async("sendMailExecutor")
    public void sendResetPasswordMail(String targetEmail, String destinationUrl,String token) {

        try {
            //템플릿 엔진에 넘겨줄 변수 세팅
            Context context = new Context();
            context.setVariable("destinationUrl", destinationUrl);
            context.setVariable("token", token);

            //HTML body 생성
            String html = templateEngine.process("user/reset_password_email", context);

            //message 준비
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, MULTIPART_MODE_NO, UTF_8);
            messageHelper.setSubject("bluesky 비밀번호 재설정 안내");
            messageHelper.setFrom("shawnlsj09@gmail.com","bluesky");
            messageHelper.setTo(targetEmail);
            messageHelper.setText(html, true);

            //메일 전송
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("create mail failure", e);
            throw new MailSendingException(e);
        } catch (MailException e) {
            log.error("send mail failure", e);
            throw new MailSendingException(e);
        }
    }
}
