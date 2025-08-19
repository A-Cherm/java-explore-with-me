package ru.practicum.ewm.admin.service;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.user.NewUserDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.QUser;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        QUser user = QUser.user;
        JPAQuery<User> jpaQuery = queryFactory.selectFrom(user);

        if (ids != null) {
            jpaQuery.where(user.id.in(ids));
        }
        jpaQuery.offset(from).limit(size);

        List<User> users = jpaQuery.fetch();

        return users.stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserDto userDto) {
        User user = userRepository.save(UserMapper.mapToUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        validateUser(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Некорректный id пользователя",
                        "Нет пользователя с id = " + userId));
    }
}
