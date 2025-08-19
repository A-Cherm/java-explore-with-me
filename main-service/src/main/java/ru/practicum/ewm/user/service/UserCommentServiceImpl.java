package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.admin.service.UserService;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.guest.service.GuestEventService;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CommentRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommentServiceImpl implements UserCommentService {
    private final CommentRepository commentRepository;
    private final GuestEventService guestEventService;
    private final UserService userService;

    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto commentDto) {
        User user = userService.validateUser(userId);
        Event event = guestEventService.validateEvent(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new DataConflictException("Некорректные условия запроса",
                    "Нельзя комментировать неопубликованное событие");
        }
        Comment newComment = commentRepository
                .save(CommentMapper.mapToComment(commentDto, user, eventId));

        return CommentMapper.mapToCommentDto(newComment);
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto commentDto) {
        Comment comment = validateComment(commentId);

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new DataConflictException("Некорректные условия запроса",
                    "Нельзя редактировать чужой комментарий");
        }
        comment.setText(commentDto.getText());
        comment = commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(comment);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = validateComment(commentId);

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new DataConflictException("Некорректные условия запроса",
                    "Нельзя удалить чужой комментарий");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment validateComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Не найден комментарий",
                        "Нет комментария с id = " + commentId));
    }
}
