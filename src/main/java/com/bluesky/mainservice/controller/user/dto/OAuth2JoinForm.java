package com.bluesky.mainservice.controller.user.dto;

import com.bluesky.mainservice.controller.validation.Nickname;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuth2JoinForm {

    @Nickname
    String nickname;
}
