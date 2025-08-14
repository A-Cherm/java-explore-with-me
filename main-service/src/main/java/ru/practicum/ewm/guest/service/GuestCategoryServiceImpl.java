package ru.practicum.ewm.guest.service;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.QCategory;
import ru.practicum.ewm.repository.CategoryRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GuestCategoryServiceImpl implements GuestCategoryService {
    private final CategoryRepository categoryRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        QCategory category = QCategory.category;
        JPAQuery<Category> jpaQuery = queryFactory.selectFrom(category);

        if (from != null) {
            jpaQuery.offset(from);
        }
        if (size != null) {
            jpaQuery.limit(size);
        }
        List<Category> categories = jpaQuery.fetch();

        return categories.stream()
                .map(CategoryMapper::mapToCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        Category category = validateCategory(catId);

        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    public Category validateCategory(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Не найдена категория",
                        "Нет категории с id = " + catId));
    }
}
