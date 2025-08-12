package ru.practicum.ewm.admin.service;

import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto createUser(UserDto userDto);

    void deleteUser(Long userId);

    User validateUser(Long userId);
}
