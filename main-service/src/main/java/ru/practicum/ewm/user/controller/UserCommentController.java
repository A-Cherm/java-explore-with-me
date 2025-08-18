package ru.practicum.ewm.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.user.service.UserCommentService;

@RestController
@RequestMapping("/users/{userId}")
@Slf4j
@RequiredArgsConstructor
public class UserCommentController {
    private final UserCommentService commentService;

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody NewCommentDto commentDto) {
        CommentDto comment = commentService.createComment(userId, eventId, commentDto);

        log.info("Создан комментарий {}", comment);
        return comment;
    }

    @PatchMapping("/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @Valid @RequestBody UpdateCommentDto commentDto) {
        CommentDto comment = commentService.updateComment(userId, commentId, commentDto);

        log.info("Обновлён комментарий с id = {}: {}", commentId, commentDto);
        return comment;
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        commentService.deleteComment(userId, commentId);
        log.info("Удалён комментарий с id = {}", commentId);
    }
}
