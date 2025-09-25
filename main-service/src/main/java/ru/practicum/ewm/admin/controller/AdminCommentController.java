package ru.practicum.ewm.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.service.AdminCommentService;
import ru.practicum.ewm.dto.comment.CommentDto;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@Slf4j
@Validated
@RequiredArgsConstructor
@Tag(name = "Admin: комментарии", description = "Просмотр и удаление комментариев")
public class AdminCommentController {
    private final AdminCommentService adminCommentService;

    @GetMapping
    @Operation(summary = "Просмотр комментариев",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content)
            })
    public List<CommentDto> getComments(
            @RequestParam(required = false) @Parameter(description = "Список id событий") List<Long> events,
            @RequestParam(required = false) @Parameter(description = "Дата начала интервала времени") String rangeStart,
            @RequestParam(required = false) @Parameter(description = "Дата конца интервала времени") String rangeEnd,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size
    ) {
        List<CommentDto> comments = adminCommentService.getComments(events, rangeStart, rangeEnd, from, size);

        log.info("Возвращаются комментарии: {}", comments);
        return comments;
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление комментария",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Комментарий удалён"),
                    @ApiResponse(responseCode = "404", description = "Нет комментария с данным id", content = @Content)
            })
    public void deleteComment(@PathVariable @Parameter(description = "Id комментария") Long commentId) {
        adminCommentService.deleteComment(commentId);
        log.info("Удалён комментарий с id = {}", commentId);
    }
}
