package com.bluesky.mainservice.controller.board;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
class BoardSaveForm {
    @NotBlank(message = "제목을 1자 이상 입력하여 주세요.")
    @Size(min = 1, max = Options.MAX_TITLE_SIZE, message = "제한 길이 초과")
    private String title;

    @NotBlank(message = "본문을 1자 이상 입력하여 주세요.")
    @Size(min = 1, max = Options.MAX_CONTENT_SIZE, message = "제한 길이 초과")
    private String content;
}
