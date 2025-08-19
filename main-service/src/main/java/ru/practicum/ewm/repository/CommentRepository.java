package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {
    List<Comment> findAllByEventIdOrderByCreatedDesc(Long eventId);

    List<Comment> findAllByEventIdInOrderByCreatedDesc(List<Long> ids);
}
