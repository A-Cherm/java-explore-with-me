package ru.practicum.ewm.guest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Guest: категории", description = "Просмотр категорий событий")
public class GuestCategoryController {
    private final GuestCategoryService categoryService;

    @GetMapping
    @Operation(summary = "Получение списка категорий")
    public List<CategoryDto> getCategories(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        List<CategoryDto> categories = categoryService.getCategories(from, size);

        log.info("Возвращаются категории: {}", categories);
        return categories;
    }

    @GetMapping("/{catId}")
    @Operation(summary = "Получение категории",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Нет категории с данным id", content = @Content)
            })
    public CategoryDto getCategory(@PathVariable @Parameter(description = "Id категории") Long catId) {
        CategoryDto category = categoryService.getCategory(catId);

        log.info("Возвращается категория: {}", category);
        return category;
    }
}
