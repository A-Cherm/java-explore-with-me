package ru.practicum.ewm.guest.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.ewm.admin.service.AdminCategoryService;
import ru.practicum.ewm.admin.service.AdminCategoryServiceImpl;
import ru.practicum.ewm.config.QuerydslConfig;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Category;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({GuestCategoryServiceImpl.class, AdminCategoryServiceImpl.class, QuerydslConfig.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GuestCategoryServiceImplTest {
    private final GuestCategoryService guestCategoryService;
    private final AdminCategoryService adminCategoryService;

    @Test
    void testGetCategories() {
        CategoryDto categoryDto1 = new CategoryDto(null, "a");
        CategoryDto categoryDto2 = new CategoryDto(null, "b");
        CategoryDto categoryDto3 = new CategoryDto(null, "c");

        CategoryDto createdCategory1 = adminCategoryService.createCategory(categoryDto1);
        CategoryDto createdCategory2 = adminCategoryService.createCategory(categoryDto2);
        CategoryDto createdCategory3 = adminCategoryService.createCategory(categoryDto3);

        List<CategoryDto> categories = guestCategoryService.getCategories(0, 10);

        assertThat(categories).isNotNull().hasSize(3)
                .contains(createdCategory1, createdCategory2, createdCategory3);

        categories = guestCategoryService.getCategories(1, 1);

        assertThat(categories).isNotNull().hasSize(1)
                .contains(createdCategory2);
    }

    @Test
    void testGetCategory() {
        CategoryDto categoryDto = new CategoryDto(null, "a");
        CategoryDto createdCategory = adminCategoryService.createCategory(categoryDto);

        CategoryDto category = guestCategoryService.getCategory(createdCategory.getId());

        assertThat(category).isNotNull().isEqualTo(createdCategory);

        assertThrows(NotFoundException.class,
                () -> guestCategoryService.getCategory(createdCategory.getId() + 1));
    }

    @Test
    void testValidateCategory() {
        CategoryDto categoryDto = new CategoryDto(null, "a");
        CategoryDto createdCategory = adminCategoryService.createCategory(categoryDto);

        Category category = guestCategoryService.validateCategory(createdCategory.getId());

        assertThat(category).isNotNull().hasFieldOrPropertyWithValue("name", categoryDto.getName());

        assertThrows(NotFoundException.class,
                () -> guestCategoryService.validateCategory(createdCategory.getId() + 1));
    }
}