package com.bluesky.mainservice.controller.board;

import com.bluesky.mainservice.controller.validation.TextSize;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.bluesky.mainservice.controller.board.Options.*;

@Getter
@Setter
class BoardSaveForm {
    @NotBlank
    @Size(max = MAX_TITLE_SIZE)
    private String title;

    @NotBlank
    @Size(max = MAX_HTML_SIZE)
    @TextSize(max = MAX_CONTENT_SIZE)
    private String content;
}
