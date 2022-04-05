package com.bluesky.mainservice.controller.board;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class BoardForm {

    @NotBlank(message = "제목을 1자 이상 입력하여 주세요.")
    private String title;

    @NotBlank(message = "본문을 1자 이상 입력하여 주세요.")
    private String content;
}
