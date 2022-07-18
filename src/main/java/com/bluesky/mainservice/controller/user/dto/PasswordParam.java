package com.bluesky.mainservice.controller.user.dto;

import com.bluesky.mainservice.controller.validation.Password;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordParam {

    @Password
    String password;
}
