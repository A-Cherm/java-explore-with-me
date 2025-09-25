package ru.practicum.ewm.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.service.AdminCategoryService;
import ru.practicum.ewm.dto.category.CategoryDto;

@RestController
@RequestMapping("/admin/categories")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Admin: категории", description = "Управление категориями событий")
public class AdminCategoryController {
    private final AdminCategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание категории",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Категория создана"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные категории", content = @Content)
            })
    public CategoryDto createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto createdCategory = categoryService.createCategory(categoryDto);

        log.info("Категория создана: {}", createdCategory);
        return createdCategory;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление категории",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Категория удалена"),
                    @ApiResponse(responseCode = "404", description = "Нет категории с данным id")
            })
    public void deleteCategory(@PathVariable @Parameter(description = "Id категории") Long catId) {
        categoryService.deleteCategory(catId);
        log.info("Удалена категория с id = {}", catId);
    }

    @PatchMapping("/{catId}")
    @Operation(summary = "Обновление категории",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Категория обновлена"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные категории", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет категории с данным id", content = @Content)
            })
    public CategoryDto updateCategory(@Valid @RequestBody CategoryDto categoryDto,
                                      @PathVariable @Parameter(description = "Id категории") Long catId) {
        categoryDto.setId(catId);
        CategoryDto updatedCategory = categoryService.updateCategory(categoryDto);

        log.info("Обновлена категория с id = {}: {}", catId, updatedCategory);
        return updatedCategory;
    }
}
