package ru.practicum.ewm.user.service;

import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.model.Comment;

public interface UserCommentService {
    CommentDto createComment(Long userId, Long eventId, NewCommentDto commentDto);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto commentDto);

    void deleteComment(Long userId, Long commentId);

    Comment validateComment(Long commentId);
}
