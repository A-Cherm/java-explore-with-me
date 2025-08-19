package ru.practicum.ewm.guest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.guest.service.GuestCategoryService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GuestCategoryController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GuestCategoryControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private GuestCategoryService categoryService;

    @Test
    void testGetCategories() throws Exception {
        CategoryDto categoryDto = new CategoryDto(1L, "a");

        when(categoryService.getCategories(1, 10))
                .thenReturn(List.of(categoryDto));

        MvcResult result = mvc.perform(get("/categories?from=1&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        List<CategoryDto> resultList = mapper.readValue(json, new TypeReference<>() {});

        assertThat(resultList).isNotNull().contains(categoryDto);

        verify(categoryService, times(1))
                .getCategories(1, 10);
    }

    @Test
    void testGetCategory() throws Exception {
        CategoryDto categoryDto = new CategoryDto(1L, "a");

        when(categoryService.getCategory(1L))
                .thenReturn(categoryDto);

        MvcResult result = mvc.perform(get("/categories/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        CategoryDto resultCategory = mapper.readValue(json, CategoryDto.class);

        assertThat(resultCategory).isNotNull().isEqualTo(categoryDto);

        verify(categoryService, times(1))
                .getCategory(1L);
    }
}