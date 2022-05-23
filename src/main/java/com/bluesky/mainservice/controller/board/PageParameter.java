package com.bluesky.mainservice.controller.board;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Positive;

@Getter
@Setter
class PageParameter{

 @Positive
 private int page = 1;
}
