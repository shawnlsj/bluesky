package com.bluesky.mainservice.controller.user;

import com.bluesky.mainservice.config.security.jwt.JwtGenerator;
import com.bluesky.mainservice.controller.user.dto.EmailParam;
import com.bluesky.mainservice.controller.user.dto.NicknameParam;
import com.bluesky.mainservice.controller.user.dto.UserResponseDto.CheckNicknameResult;
import com.bluesky.mainservice.controller.user.email.EmailSender;
import com.bluesky.mainservice.controller.user.email.SendAuthenticationMailParam;
import com.bluesky.mainservice.controller.user.email.SendResetPasswordMailParam;
import com.bluesky.mainservice.repository.user.UserRepository;
import com.bluesky.mainservice.repository.user.constant.AccountType;
import com.bluesky.mainservice.repository.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.Optional;
import java.util.UUID;

import static com.bluesky.mainservice.controller.user.dto.UserResponseDto.SendEmailResult;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final EmailSender emailSender;
    private final UserRepository userRepository;
    private final JwtGenerator jwtGenerator;

    @GetMapping("/user/availability")
    public CheckNicknameResult checkAvailable(@Valid NicknameParam nicknameParam) {
        boolean isAvailable = userRepository.findByNickname(nicknameParam.getNickname()).isEmpty();
        return new CheckNicknameResult(isAvailable);
    }

    @PostMapping("/join/send-email")
    public SendEmailResult sendJoinEmail(@Valid EmailParam emailParam, HttpServletRequest request) {
        //이미 이메일이 등록되어 있어 있으면 이메일 전송 실패
        String email = emailParam.getEmail();
        Optional<User> user = userRepository.findByEmailAndAccountType(email, AccountType.ORIGINAL);
        if (user.isPresent()) {
            return new SendEmailResult(false);
        }

        //인증메일 발송
        String destinationUrl = "https://" + request.getServerName() + "/join";
        String token = jwtGenerator.generateJoinToken(email, AccountType.ORIGINAL);
        SendAuthenticationMailParam sendAuthenticationMailParam = SendAuthenticationMailParam.builder()
                .targetEmail(email)
                .destinationUrl(destinationUrl)
                .token(token)
                .serverName(request.getServerName())
                .build();
        emailSender.sendAuthenticationMail(sendAuthenticationMailParam);
        return new SendEmailResult(true);
    }

    @PostMapping("/reset-password/send-email")
    public SendEmailResult sendPasswordResetEmail(@Valid EmailParam emailParam, HttpServletRequest request) {
        //등록된 이메일이 아니면 이메일 전송 실패
        String email = emailParam.getEmail();

        UUID userId = userRepository
                .findByEmailAndAccountType(email, AccountType.ORIGINAL)
                .map(User::getUuid)
                .orElse(null);

        if (userId == null) {
            return new SendEmailResult(false);
        }

        //인증메일 발송
        String destinationUrl = "https://" + request.getServerName() + "/reset-password";
        String token = jwtGenerator.generateResetPasswordToken(userId);
        SendResetPasswordMailParam sendResetPasswordMailParam = SendResetPasswordMailParam.builder()
                .targetEmail(email)
                .destinationUrl(destinationUrl)
                .token(token)
                .serverName(request.getServerName())
                .build();
        emailSender.sendResetPasswordMail(sendResetPasswordMailParam);
        return new SendEmailResult(true);
    }
}
