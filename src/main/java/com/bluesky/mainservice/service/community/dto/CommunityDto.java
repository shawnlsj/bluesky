package com.bluesky.mainservice.service.community.dto;

import com.bluesky.mainservice.repository.community.constant.CommunityType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class CommunityDto {

    private final CommunityType communityType;
    private final String communityName;
    private final Map<Long, String> boardDirectoryMap;
}
