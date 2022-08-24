package com.bluesky.mainservice.controller.argument;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class LoginUser {
    private final UUID id;
}
