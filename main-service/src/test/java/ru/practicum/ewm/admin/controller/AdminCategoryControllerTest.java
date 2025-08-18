package ru.practicum.ewm.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.ewm.admin.service.AdminCategoryService;
import ru.practicum.ewm.dto.category.CategoryDto;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCategoryController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AdminCategoryControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private AdminCategoryService categoryService;

    @Test
    void testCreateCategory() throws Exception {
        CategoryDto categoryDto = new CategoryDto(null, "a");
        CategoryDto createdCategoryDto = new CategoryDto(1L, "a");

        when(categoryService.createCategory(categoryDto))
                .thenReturn(createdCategoryDto);

        MvcResult result = mvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(categoryDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        CategoryDto resultCategory = mapper.readValue(json, CategoryDto.class);

        assertThat(resultCategory).isNotNull().isEqualTo(createdCategoryDto);

        verify(categoryService, times(1))
                .createCategory(categoryDto);
    }

    @Test
    void testDeleteCategory() throws Exception {
        mvc.perform(delete("/admin/categories/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1))
                .deleteCategory(1L);
    }

    @Test
    void testUpdateCategory() throws Exception {
        CategoryDto categoryDto = new CategoryDto(1L, "a");
        CategoryDto updatedCategoryDto = new CategoryDto(1L, "a");

        when(categoryService.updateCategory(categoryDto))
                .thenReturn(updatedCategoryDto);

        MvcResult result = mvc.perform(patch("/admin/categories/1")
                        .content(mapper.writeValueAsString(categoryDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        CategoryDto resultCategory = mapper.readValue(json, CategoryDto.class);

        assertThat(resultCategory).isNotNull().isEqualTo(updatedCategoryDto);

        verify(categoryService, times(1))
                .updateCategory(categoryDto);
    }
}