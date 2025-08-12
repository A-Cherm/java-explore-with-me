package ru.practicum.ewm.admin.service;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.QUser;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        if (from != null && from < 0) {
            throw new ValidationException("Некорректный параметр",
                    "Параметр не может быть отрицательным: from = " + from);
        }
        if (size != null && size <= 0) {
            throw new ValidationException("Некорректный параметр",
                    "Параметр может быть только положительным: size = " + size);
        }
        QUser user = QUser.user;
        JPAQuery<User> jpaQuery = queryFactory.selectFrom(user);

        if (ids != null) {
            jpaQuery.where(user.id.in(ids));
        }
        if (from != null) {
            jpaQuery.offset(from);
        }
        if (size != null) {
            jpaQuery.limit(size);
        }
        List<User> users = jpaQuery.fetch();

        return users.stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.mapToUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        validateUser(userId);
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Некорректный id пользователя",
                        "Нет пользователя с id = " + userId));
    }
}
