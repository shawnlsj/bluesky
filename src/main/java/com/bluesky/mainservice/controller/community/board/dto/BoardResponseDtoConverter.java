package com.bluesky.mainservice.controller.community.board.dto;

import com.bluesky.mainservice.controller.community.board.constant.SearchType;
import com.bluesky.mainservice.repository.community.board.dto.*;
import com.bluesky.mainservice.service.community.board.dto.BoardContent;
import com.bluesky.mainservice.service.community.board.dto.ReplyDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bluesky.mainservice.controller.community.board.dto.BoardResponseDto.*;

public class BoardResponseDtoConverter {

    public static List<SimpleBoardSignature> toSimpleBoardSignature(List<BoardTitleNoUserDto> list) {
        return list.stream()
                .map(dto -> SimpleBoardSignature.builder()
                        .communityType(dto.getCommunityType())
                        .boardDirectoryId(dto.getDirectoryId())
                        .boardDirectoryName(dto.getDirectoryName())
                        .id(dto.getId())
                        .title(dto.getTitle())
                        .replyCount(convertReplyCount(dto.getReplyCount()))
                        .build())
                .collect(Collectors.toList());

    }

    public static List<BoardSearchResult> toBoardSearchResultList(List<BoardTextContentDto> list,
                                                                  String keyword,
                                                                  SearchType searchType) {
        return list.stream()
                .map(dto -> {
                    String title = dto.getTitle();
                    String content = dto.getContent();
                    String nickname = dto.getNickname();
                    switch (searchType) {
                        case TITLE_CONTENT:
                            title = markKeyword(title, keyword);
                            content = toMarkedSubstring(content, keyword);
                            nickname = htmlEscape(nickname);
                            break;
                        case TITLE:
                            title = markKeyword(title, keyword);
                            content = toSubstring(content);
                            nickname = htmlEscape(nickname);
                            break;
                        case WRITER:
                            title = htmlEscape(title);
                            content = toSubstring(content);
                            nickname = markKeyword(nickname, keyword);
                            break;
                        case REPLY:
                            title = htmlEscape(title);
                            content = toSubstring(content);
                            nickname = htmlEscape(nickname);
                            break;
                    }

                    return BoardSearchResult.builder()
                            .id(dto.getId())
                            .communityType(dto.getCommunityType())
                            .boardDirectoryId(dto.getBoardDirectoryId())
                            .boardDirectoryName(dto.getBoardDirectoryName())
                            .title(title)
                            .content(content)
                            .createdDate(dto.getCreatedDate())
                            .userId(dto.getUserId())
                            .profileImage(dto.getProfileImage())
                            .nickname(nickname)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public static BoardDirectoryNavigation toBoardDirectoryNavigation(List<BoardDirectoryDto> list, Integer currentDirectoryId) {
        Map<Integer, String> idAndNameMap = new HashMap<>();
        for (BoardDirectoryDto dto : list) {
            idAndNameMap.put(dto.getId(), dto.getName());
        }
        return new BoardDirectoryNavigation(idAndNameMap, currentDirectoryId);
    }

    public static List<BoardOutsideView> toBoardOutsideViewList(List<BoardTitleDto> list, String keyword, SearchType searchType) {
        return list.stream()
                .map(dto -> {
                    String title = dto.getTitle();
                    String nickname = dto.getNickname();
                    if (searchType != null && keyword != null) {
                        switch (searchType) {
                            case TITLE_CONTENT:
                            case TITLE:
                                title = markKeyword(title, keyword);
                                nickname = htmlEscape(nickname);
                                break;
                            case WRITER:
                                title = htmlEscape(title);
                                nickname = markKeyword(nickname, keyword);
                                break;
                            case REPLY:
                                title = htmlEscape(title);
                                nickname = htmlEscape(nickname);
                                break;
                        }
                    } else {
                        title = htmlEscape(title);
                        nickname = htmlEscape(nickname);
                    }

                    return BoardOutsideView.builder()
                            .id(dto.getId())
                            .categoryId(dto.getCategoryId())
                            .categoryName(dto.getCategoryName())
                            .title(title)
                            .userId(dto.getUserId())
                            .nickname(nickname)
                            .profileImage(dto.getProfileImage())
                            .viewCount(dto.getViewCount())
                            .replyCount(dto.getReplyCount())
                            .likesCount(dto.getLikesCount())
                            .createdDate(dto.getCreatedDate())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public static BoardInsideView toBoardInsideViewList(BoardContent board, boolean likesClicked) {
        return BoardInsideView.builder()
                .id(board.getId())
                .category(board.getCategory())
                .userId(board.getUserId())
                .nickname(board.getNickname())
                .profileImage(board.getProfileImage())
                .title(board.getTitle())
                .content(board.getContent())
                .viewCount(board.getViewCount())
                .replyCount(board.getReplyCount())
                .likesCount(board.getLikesCount())
                .createdDate(board.getCreatedDate())
                .likesClicked(likesClicked)
                .build();
    }

    public static BoardModifyView toBoardModifyView(BoardContent board) {
        String content = board.getContent()
                .replace(" ", "&nbsp;")
                .replaceAll("(?<=<[^>]*)&nbsp;(?=[^<]*>)", " ");
        return BoardModifyView.builder()
                .id(board.getId())
                .categoryId(board.getCategoryId())
                .title(board.getTitle())
                .content(content)
                .build();
    }

    public static List<ReplySearchResult> toReplySearchResultList(List<ReplySearchResultDto> list, String keyword) {
        return list.stream()
                .map(dto -> ReplySearchResult.builder()
                        .boardId(dto.getBoardId())
                        .content(toMarkedSubstring(dto.getContent(), keyword))
                        .createdDate(dto.getCreatedDate())
                        .userId(dto.getUserId())
                        .profileImage(dto.getProfileImage())
                        .nickname(dto.getNickname())
                        .build())
                .collect(Collectors.toList());
    }

    public static List<ReplyInsideView> toReplyInsideViewList(List<ReplyDto> list) {
        List<ReplyInsideView> replyInsideViewList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ReplyDto reply = list.get(i);
            boolean isLastOfGroup = false;
            //리스트의 마지막 요소가 아닌 경우
            if (i + 1 < list.size()) {
                //다음 요소의 그룹 id 와 자신의 그룹 id가 다르면
                //자신은 해당 그룹에서 마지막 댓글이다
                long nextGroupId = list.get(i + 1).getGroupId();
                if (reply.getGroupId() != nextGroupId) {
                    isLastOfGroup = true;
                }

            } else {
                //리스트의 마지막 요소인 경우
                //자신은 해당 그룹에서 마지막 댓글이다
                isLastOfGroup = true;
            }

            String deletedBy = "";
            switch (reply.getReplyState()) {
                case DELETED_BY_ADMIN:
                    deletedBy = "ADMIN";
                    break;
                case DELETED_BY_USER:
                    deletedBy = "USER";
                    break;
            }

            //삭제된 댓글의 경우에는 필수 값들만 바인딩
            //아닌 경우에는 모든 값을 바인딩
            if (StringUtils.hasText(deletedBy)) {
                replyInsideViewList.add(ReplyInsideView
                        .builder()
                        .id(reply.getId())
                        .userId(reply.getUserId())
                        .nickname(reply.getNickname())
                        .profileImage(reply.getProfileImage())
                        .isTopLevel(reply.isTopLevel())
                        .isLastOfGroup(isLastOfGroup)
                        .deletedBy(deletedBy)
                        .build());

            } else {
                replyInsideViewList.add(ReplyInsideView
                        .builder()
                        .id(reply.getId())
                        .content(reply.getContent())
                        .likesCount(reply.getLikesCount())
                        .createdDate(reply.getCreatedDate())
                        .userId(reply.getUserId())
                        .nickname(reply.getNickname())
                        .profileImage(reply.getProfileImage())
                        .isTopLevel(reply.isTopLevel())
                        .parentReplyUserId(reply.getParentReplyUserId())
                        .parentReplyUserNickname(reply.getParentReplyUserNickname())
                        .isLikesClicked(reply.isLikesClicked())
                        .isLastOfGroup(isLastOfGroup)
                        .deletedBy(deletedBy)
                        .build());
            }

        }
        return replyInsideViewList;
    }

    public static List<BoardCountStatus> toBoardCountStatus(List<BoardDirectoryWithBoardCount> list) {
        return list.stream().map(
                dto -> BoardCountStatus.builder()
                        .communityType(dto.getCommunityType())
                        .boardDirectoryId(dto.getBoardDirectoryId())
                        .boardDirectoryName(dto.getBoardDirectoryName())
                        .boardCount(convertBoardCount(dto.getBoardCount()))
                        .build()
        ).collect(Collectors.toList());
    }

    private static String htmlEscape(String str) {
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static String markKeyword(String unescapedContent, String keyword) {
        String escapedKeyword = htmlEscape(keyword);
        return unescapedContent
                .replace(keyword, "<mark>" + keyword + "</mark>")
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("&lt;mark&gt;" + escapedKeyword + "&lt;/mark&gt;",
                        "<mark>" + escapedKeyword + "</mark>");
    }

    private static String toSubstring(String unescapedContent) {
        if (unescapedContent.length() > 100) {
            String contentSubstring = unescapedContent.substring(0, 100);
            return htmlEscape(contentSubstring).concat("...");
        }
        return htmlEscape(unescapedContent);
    }

    private static String toMarkedSubstring(String unescapedContent, String keyword) {
        if (unescapedContent.length() > 100) {
            String contentSubstring = unescapedContent.substring(0, 100);
            return markKeyword(contentSubstring, keyword).concat("...");
        }
        return markKeyword(unescapedContent, keyword);
    }

    private static String convertBoardCount(long count) {
        if (count > 999) {
            return 999 + "+";
        }
        return String.valueOf(count);
    }

    private static String convertReplyCount(int count) {
        if (count > 99) {
            return 99 + "+";
        }
        return String.valueOf(count);
    }
//    public static List<BoardSimpleOutsideView> toBoardSimpleOutsideViewList(List<BoardNoContentAndWriterDto> list) {
//        return list.stream()
//                .map(dto -> BoardSim)
//    }
}
