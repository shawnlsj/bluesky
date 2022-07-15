package com.bluesky.mainservice.controller.user;

import com.bluesky.mainservice.controller.user.dto.EmailParam;
import com.bluesky.mainservice.controller.user.dto.NicknameParam;
import com.bluesky.mainservice.controller.user.dto.UserResponseDto.CheckNicknameResult;
import com.bluesky.mainservice.controller.util.EmailSender;
import com.bluesky.mainservice.repository.user.UserRepository;
import com.bluesky.mainservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.bluesky.mainservice.controller.user.dto.UserResponseDto.SendEmailResult;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final EmailSender emailSender;
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/user/availability")
    public CheckNicknameResult checkAvailable(@Valid NicknameParam nicknameParam) {
        boolean isAvailable = (userRepository.findByNickname(nicknameParam.getNickname()) == null);
        return new CheckNicknameResult(isAvailable);
    }

    @PostMapping("/join/send-email")
    public SendEmailResult verifyEmail(@Valid EmailParam emailParam, HttpServletRequest request) {
        //이미 등록된 이메일인지 확인
        String email = emailParam.getEmail();
        boolean isNewUser = userService.isNewOriginalUser(email);
        if (!isNewUser) {
            return new SendEmailResult(false);
        }

        //인증메일 발송
        String destinationUrl = "https://" + request.getServerName() + "/join";
        emailSender.sendAuthenticationMail(email, destinationUrl);
        return new SendEmailResult(true);
    }
}
