package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                UserMapper.mapToUserShortDto(comment.getAuthor()),
                comment.getText(),
                comment.getCreated()
        );
    }

    public static Comment mapToComment(NewCommentDto commentDto , User user, Long eventId) {
        return new Comment(
                null,
                user,
                eventId,
                commentDto.getText(),
                LocalDateTime.now()
        );
    }
}
