package ru.practicum.ewm.admin.service;

import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.model.Category;

public interface AdminCategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(CategoryDto categoryDto);

    Category validateCategory(Long catId);
}
