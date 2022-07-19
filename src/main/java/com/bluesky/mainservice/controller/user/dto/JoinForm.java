package com.bluesky.mainservice.controller.user.dto;

import com.bluesky.mainservice.controller.validation.Nickname;
import com.bluesky.mainservice.controller.validation.Password;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinForm {

    @Nickname
    private String nickname;

    @Password
    private String password;
}
