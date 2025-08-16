package ru.practicum.ewm.admin.controller;

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
import ru.practicum.ewm.admin.service.UserService;
import ru.practicum.ewm.dto.NewUserDto;
import ru.practicum.ewm.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminUserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AdminUserControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private UserService userService;

    @Test
    void testGetUsers() throws Exception {
        UserDto userDto = new UserDto(1L, "a", "a@mail");

        when(userService.getUsers(List.of(1L, 2L), 0, 3))
                .thenReturn(List.of(userDto));

        MvcResult result = mvc.perform(get("/admin/users?ids=1,2&from=0&size=3")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        List<UserDto> userList = mapper.readValue(json, new TypeReference<>() {});

        assertThat(userList).isNotNull().hasSize(1).contains(userDto);

        verify(userService, times(1))
                .getUsers(List.of(1L, 2L), 0, 3);
    }

    @Test
    void testCreateUser() throws Exception {
        NewUserDto userDto = new NewUserDto("aa", "a@mail");
        UserDto createdUserDto = new UserDto(1L, "aa", "a@mail");

        when(userService.createUser(userDto))
                .thenReturn(createdUserDto);

        MvcResult result = mvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        UserDto response = mapper.readValue(json, UserDto.class);

        assertThat(response).isNotNull().isEqualTo(createdUserDto);

        verify(userService, times(1))
                .createUser(userDto);
    }

    @Test
    void testDeleteUser() throws Exception {
        mvc.perform(delete("/admin/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .deleteUser(1L);
    }
}