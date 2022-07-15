package com.bluesky.mainservice.controller.argument;

import lombok.Getter;

@Getter
public class MobilePageArgument {

    private final boolean isRequested;

    public MobilePageArgument(final boolean isRequested) {
        this.isRequested = isRequested;
    }
}
