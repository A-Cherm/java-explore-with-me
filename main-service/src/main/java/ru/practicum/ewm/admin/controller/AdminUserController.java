package ru.practicum.ewm.admin.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.service.UserService;
import ru.practicum.ewm.dto.user.NewUserDto;
import ru.practicum.ewm.dto.user.UserDto;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        List<UserDto> users = userService.getUsers(ids, from, size);

        log.info("Возвращаются пользователи: {}", users);
        return users;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);

        log.info("Создан пользователь {}", createdUser);
        return createdUser;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        log.info("Удалён пользователь с id = {}", userId);
    }
}
