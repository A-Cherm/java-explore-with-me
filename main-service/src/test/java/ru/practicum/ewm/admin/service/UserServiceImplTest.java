package ru.practicum.ewm.admin.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.ewm.config.QuerydslConfig;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({UserServiceImpl.class, QuerydslConfig.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final UserService userService;

    @Test
    void testGetUsers() {
        UserDto userDto1 = new UserDto(null, "a", "a@mail");
        UserDto userDto2 = new UserDto(null, "b", "b@mail");
        UserDto userDto3 = new UserDto(null, "c", "c@mail");

        UserDto createdUser1 = userService.createUser(userDto1);
        UserDto createdUser2 = userService.createUser(userDto2);
        UserDto createdUser3 = userService.createUser(userDto3);

        List<UserDto> users = userService.getUsers(null, 0, 10);

        assertThat(users).isNotNull().hasSize(3)
                .contains(createdUser1, createdUser2, createdUser3);

        users = userService.getUsers(List.of(createdUser1.getId(), createdUser3.getId()), 0, 10);

        assertThat(users).isNotNull().hasSize(2)
                .contains(createdUser1, createdUser3);

        users = userService.getUsers(List.of(createdUser3.getId(), createdUser3.getId() + 1), 0, 10);

        assertThat(users).isNotNull().hasSize(1)
                .contains(createdUser3);

        users = userService.getUsers(null, 1, 10);

        assertThat(users).isNotNull().hasSize(2)
                .contains(createdUser2, createdUser3);

        users = userService.getUsers(null, 1, 1);

        assertThat(users).isNotNull().hasSize(1)
                .contains(createdUser2);
    }

    @Test
    void testCreateUser() {
        UserDto userDto = new UserDto(null, "a", "a@mail");
        UserDto createdUser = userService.createUser(userDto);

        assertThat(createdUser)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(userDto);
    }

    @Test
    void testDeleteUser() {
        UserDto userDto = new UserDto(null, "a", "a@mail");
        UserDto createdUser = userService.createUser(userDto);
        userService.deleteUser(createdUser.getId());

        List<UserDto> users = userService.getUsers(null, 0, 10);

        assertThat(users).isNotNull().isEmpty();
    }

    @Test
    void testValidateUser() {
        UserDto userDto = new UserDto(null, "a", "a@mail");
        UserDto createdUser = userService.createUser(userDto);

        User user = userService.validateUser(createdUser.getId());

        assertThat(user)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "a")
                .hasFieldOrPropertyWithValue("email", "a@mail");
        assertThrows(NotFoundException.class,
                () -> userService.validateUser(createdUser.getId() + 1));
    }
}