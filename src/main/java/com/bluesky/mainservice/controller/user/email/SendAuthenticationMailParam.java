package com.bluesky.mainservice.controller.user.email;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class SendAuthenticationMailParam {

    private final String targetEmail;
    private final String destinationUrl;
    private final String token;
    private final String serverName;

    @Builder
    private SendAuthenticationMailParam(@NonNull String targetEmail,
                                        @NonNull String destinationUrl,
                                        @NonNull String token,
                                        @NonNull String serverName) {
        this.targetEmail = targetEmail;
        this.destinationUrl = destinationUrl;
        this.token = token;
        this.serverName = serverName;
    }
}
