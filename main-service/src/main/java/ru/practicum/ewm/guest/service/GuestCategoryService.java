package ru.practicum.ewm.guest.service;

import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.model.Category;

import java.util.List;

public interface GuestCategoryService {
    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategory(Long catId);

    Category validateCategory(Long catId);
}