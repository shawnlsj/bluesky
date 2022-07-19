package com.bluesky.mainservice.controller.user.dto;

import com.bluesky.mainservice.controller.validation.Email;
import lombok.Getter;

@Getter
public class EmailParam {

    @Email
    String email;

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }
}
