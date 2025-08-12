package ru.practicum.ewm.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.service.UserService;
import ru.practicum.ewm.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Slf4j
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(required = false) Integer from,
                                  @RequestParam(required = false) Integer size) {
        List<UserDto> users = userService.getUsers(ids, from, size);

        log.info("Возвращаются пользователи: {}", users);
        return users;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);

        log.info("Создан пользователь {}", createdUser);
        return createdUser;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        log.info("Удалён пользователь с id = {}", userId);
    }
}
