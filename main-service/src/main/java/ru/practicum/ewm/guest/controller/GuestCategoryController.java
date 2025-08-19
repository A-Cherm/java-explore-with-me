package ru.practicum.ewm.guest.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.guest.service.GuestCategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Slf4j
@Validated
@RequiredArgsConstructor
public class GuestCategoryController {
    private final GuestCategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        List<CategoryDto> categories = categoryService.getCategories(from, size);

        log.info("Возвращаются категории: {}", categories);
        return categories;
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        CategoryDto category = categoryService.getCategory(catId);

        log.info("Возвращается категория: {}", category);
        return category;
    }
}
