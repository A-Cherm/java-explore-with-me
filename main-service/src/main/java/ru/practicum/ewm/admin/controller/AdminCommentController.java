package ru.practicum.ewm.admin.controller;

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
public class AdminCommentController {
    private final AdminCommentService adminCommentService;

    @GetMapping
    public List<CommentDto> getComments(@RequestParam(required = false) List<Long> events,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                        @Positive @RequestParam(defaultValue = "10") Integer size) {
        List<CommentDto> comments = adminCommentService.getComments(events, rangeStart, rangeEnd, from, size);

        log.info("Возвращаются комментарии: {}", comments);
        return comments;
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        adminCommentService.deleteComment(commentId);
        log.info("Удалён комментарий с id = {}", commentId);
    }
}
