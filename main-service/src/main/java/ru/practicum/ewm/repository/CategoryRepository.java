package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>, QuerydslPredicateExecutor<Category> {
}
