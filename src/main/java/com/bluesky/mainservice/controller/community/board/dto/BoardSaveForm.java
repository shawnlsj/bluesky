package com.bluesky.mainservice.controller.community.board.dto;

import com.bluesky.mainservice.controller.community.board.validation.BoardContent;
import com.bluesky.mainservice.controller.community.board.validation.BoardTitle;
import com.bluesky.mainservice.repository.community.constant.CommunityType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class BoardSaveForm {

    @BoardTitle
    private String title;

    @BoardContent
    private String content;

    //뷰로 넘어가기 전에 반드시 값이 들어있어야 함
    @NotNull
    private CommunityType communityType;

    //뷰로 넘어가기 전에 반드시 값이 들어있어야 함
    @NotNull
    @Positive
    private Integer directoryId;

    @NotNull
    @Positive
    private Integer categoryId;

    public BoardSaveForm(CommunityType communityType, Integer directoryId) {
        this.communityType = communityType;
        this.directoryId = directoryId;
    }
}
