package com.bluesky.mainservice.controller.user.dto;

import com.bluesky.mainservice.controller.user.validation.Nickname;
import com.bluesky.mainservice.controller.user.validation.Password;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSaveForm {

    @Nickname
    private String nickname;

    @Password
    private String password;
}
