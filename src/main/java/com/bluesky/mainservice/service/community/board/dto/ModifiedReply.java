package com.bluesky.mainservice.service.community.board.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ModifiedReply {

    private final Long id;
    private final String content;
    private final UUID userId;
}
