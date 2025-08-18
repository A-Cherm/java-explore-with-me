package ru.practicum.ewm.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.save(CategoryMapper.mapToCategory(categoryDto));

        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    public void deleteCategory(Long catId) {
        validateCategory(catId);
        if (eventRepository.countByCategoryId(catId) > 0) {
            throw new DataConflictException("Некорректные условия операции",
                    "У категории с id = " + catId + " есть связанные события");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        validateCategory(categoryDto.getId());
        Category category = categoryRepository.save(CategoryMapper.mapToCategory(categoryDto));

        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Category validateCategory(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Не найдена категория",
                        "Нет категории с id = " + catId));
    }
}
