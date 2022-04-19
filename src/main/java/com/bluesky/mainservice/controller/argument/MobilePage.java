package com.bluesky.mainservice.controller.argument;

import lombok.Getter;

@Getter
public class MobilePage {
    private final boolean isRequested;
    private MobilePage(final boolean isRequested) {
        this.isRequested = isRequested;
    }

    public static MobilePage createAndSet(final boolean isRequested) {
        return new MobilePage(isRequested);
    }
}
