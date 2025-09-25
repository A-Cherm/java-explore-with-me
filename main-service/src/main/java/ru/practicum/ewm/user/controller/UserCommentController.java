package ru.practicum.ewm.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User: комментарии", description = "Управление комментариями")
public class UserCommentController {
    private final UserCommentService commentService;

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание комментария",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Комментарий создан"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет события с данным id", content = @Content)
            })
    public CommentDto createComment(@PathVariable @Parameter(description = "Id пользователя") Long userId,
                                    @PathVariable @Parameter(description = "Id события") Long eventId,
                                    @Valid @RequestBody NewCommentDto commentDto) {
        CommentDto comment = commentService.createComment(userId, eventId, commentDto);

        log.info("Создан комментарий {}", comment);
        return comment;
    }

    @PatchMapping("/comments/{commentId}")
    @Operation(summary = "Обновление комментария",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет комментария с данным id", content = @Content)
            })
    public CommentDto updateComment(@PathVariable @Parameter(description = "Id пользователя") Long userId,
                                    @PathVariable @Parameter(description = "Id комментария") Long commentId,
                                    @Valid @RequestBody UpdateCommentDto commentDto) {
        CommentDto comment = commentService.updateComment(userId, commentId, commentDto);

        log.info("Обновлён комментарий с id = {}: {}", commentId, commentDto);
        return comment;
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление комментария",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Комментарий удалён"),
                    @ApiResponse(responseCode = "404", description = "Нет комментария с данным id", content = @Content)
            })
    public void deleteComment(@PathVariable @Parameter(description = "Id пользователя") Long userId,
                              @PathVariable @Parameter(description = "Id комментария") Long commentId) {
        commentService.deleteComment(userId, commentId);
        log.info("Удалён комментарий с id = {}", commentId);
    }
}
