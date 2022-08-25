package com.bluesky.mainservice.config.converter;

import com.bluesky.mainservice.controller.community.board.constant.SearchType;
import org.springframework.core.convert.converter.Converter;

public class StringToSearchTypeConverter implements Converter<String, SearchType> {

    @Override
    public SearchType convert(String source) {
        return SearchType.valueOf(source.toUpperCase());
    }
}
