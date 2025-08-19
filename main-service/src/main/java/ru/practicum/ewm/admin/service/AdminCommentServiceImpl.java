package ru.practicum.ewm.admin.service;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.QComment;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.user.service.UserCommentService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final CommentRepository commentRepository;
    private final UserCommentService userCommentService;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommentDto> getComments(List<Long> events, String rangeStart, String rangeEnd, Integer from, Integer size) {
        QComment comment = QComment.comment;
        JPAQuery<Comment> jpaQuery = queryFactory.selectFrom(comment);
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        try {
            if (rangeStart != null) {
                startDate = LocalDateTime.parse(UriUtils.decode(rangeStart, StandardCharsets.UTF_8), formatter);
            }
            if (rangeEnd != null) {
                endDate = LocalDateTime.parse(UriUtils.decode(rangeEnd, StandardCharsets.UTF_8), formatter);
            }
        } catch (DateTimeParseException e) {
            throw new ValidationException("Некорректный запрос",
                    "Некорректный формат дат: " + rangeStart + ", " + rangeEnd);
        }

        if (events != null) {
            jpaQuery.where(comment.eventId.in(events));
        }
        if (startDate != null) {
            jpaQuery.where(comment.created.after(startDate));
        }
        if (endDate != null) {
            jpaQuery.where(comment.created.before(endDate));
        }
        jpaQuery.orderBy(comment.created.desc())
                .offset(from)
                .limit(size);
        List<Comment> comments = jpaQuery.fetch();

        return comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        userCommentService.validateComment(commentId);
        commentRepository.deleteById(commentId);
    }
}
