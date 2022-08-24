package com.bluesky.mainservice.config.converter;

import com.bluesky.mainservice.repository.community.constant.CommunityType;
import org.springframework.core.convert.converter.Converter;

public class StringToCommunityTypeConverter implements Converter<String, CommunityType> {

    @Override
    public CommunityType convert(String source) {
        return CommunityType.valueOf(source.toUpperCase());
    }
}
