package ru.practicum.ewm.admin.service;

import ru.practicum.ewm.dto.comment.CommentDto;

import java.util.List;

public interface AdminCommentService {
    List<CommentDto> getComments(List<Long> events, String rangeStart, String rangeEnd,
                                 Integer from, Integer size);

    void deleteComment(Long commentId);
}
