package ru.practicum.ewm.admin.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.ewm.config.QuerydslConfig;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({AdminCategoryServiceImpl.class, QuerydslConfig.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AdminCategoryServiceImplTest {
    private final AdminCategoryService categoryService;

    @Test
    void testCreateCategory() {
        CategoryDto categoryDto = new CategoryDto(null, "a");
        CategoryDto createdCategory = categoryService.createCategory(categoryDto);

        assertThat(createdCategory).isNotNull().hasFieldOrPropertyWithValue("name", categoryDto.getName());
    }

    @Test
    void testDeleteCategoryWithInvalidId() {
        assertThrows(NotFoundException.class,
                () -> categoryService.deleteCategory(1L));
    }

    @Test
    void testUpdateCategory() {
        CategoryDto categoryDto = new CategoryDto(null, "a");
        CategoryDto createdCategory = categoryService.createCategory(categoryDto);
        CategoryDto updateDto = new CategoryDto(createdCategory.getId(), "b");
        CategoryDto updatedCategory = categoryService.updateCategory(updateDto);

        assertThat(updatedCategory).isNotNull().isEqualTo(updateDto);
    }

    @Test
    void testValidateCategory() {
        CategoryDto categoryDto = new CategoryDto(null, "a");
        CategoryDto createdCategory = categoryService.createCategory(categoryDto);

        Category category = categoryService.validateCategory(createdCategory.getId());

        assertThat(category).isNotNull().hasFieldOrPropertyWithValue("name", categoryDto.getName());

        assertThrows(NotFoundException.class,
                () -> categoryService.validateCategory(createdCategory.getId() + 1));
    }
}