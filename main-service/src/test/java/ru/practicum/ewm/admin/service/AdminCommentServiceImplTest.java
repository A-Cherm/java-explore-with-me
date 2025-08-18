package ru.practicum.ewm.admin.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.config.QuerydslConfig;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.user.NewUserDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.guest.service.GuestCategoryServiceImpl;
import ru.practicum.ewm.guest.service.GuestEventServiceImpl;
import ru.practicum.ewm.user.service.UserCommentService;
import ru.practicum.ewm.user.service.UserCommentServiceImpl;
import ru.practicum.ewm.user.service.UserEventService;
import ru.practicum.ewm.user.service.UserEventServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({UserCommentServiceImpl.class, UserServiceImpl.class, AdminCategoryServiceImpl.class,
        GuestEventServiceImpl.class, UserEventServiceImpl.class, AdminEventServiceImpl.class,
        AdminCommentServiceImpl.class, GuestCategoryServiceImpl.class, QuerydslConfig.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AdminCommentServiceImplTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final AdminCommentService adminCommentService;
    private final UserCommentService userCommentService;
    private final UserService userService;
    private final AdminCategoryService categoryService;
    private final UserEventService userEventService;
    private final AdminEventService adminEventService;
    @MockBean
    private StatsClient statsClient;

    private UserDto userDto1;
    private UserDto userDto2;
    private EventFullDto eventDto;
    private CommentDto commentDto1;
    private CommentDto commentDto2;
    private CommentDto commentDto3;

    @BeforeEach
    void setUp() {
        LocalDateTime date = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0, 0).plusYears(1);
        Location location = new Location((float) 10.0, (float) 20.0);
        userDto1 = userService.createUser(new NewUserDto("a", "a@mail"));
        userDto2 = userService.createUser(new NewUserDto("b", "b@mail"));
        CategoryDto categoryDto = categoryService.createCategory(new CategoryDto(null, "abc"));
        eventDto = userEventService.createEvent(userDto1.getId(),
                new NewEventDto("a".repeat(20), categoryDto.getId(), "b".repeat(20), date,
                        location, true, 5, false, "c".repeat(5)));
        UpdateEventDto updateEventDto = new UpdateEventDto();
        updateEventDto.setStateAction(EventStateAction.PUBLISH_EVENT);
        adminEventService.updateEvent(eventDto.getId(), updateEventDto);
        commentDto1 = userCommentService.createComment(userDto1.getId(), eventDto.getId(),
                new NewCommentDto("comment1"));
        commentDto2 = userCommentService.createComment(userDto1.getId(), eventDto.getId(),
                new NewCommentDto("comment2"));
        commentDto3 = userCommentService.createComment(userDto2.getId(), eventDto.getId(),
                new NewCommentDto("comment3"));
    }

    @Test
    void testGetComments() {
        List<CommentDto> comments = adminCommentService.getComments(null, null, null, 0, 3);

        assertThat(comments).hasSize(3).containsExactly(commentDto3, commentDto2, commentDto1);

        comments = adminCommentService.getComments(null, null, null, 1, 1);

        assertThat(comments).hasSize(1).contains(commentDto2);

        comments = adminCommentService.getComments(List.of(eventDto.getId()), null, null, 0, 10);

        assertThat(comments).hasSize(3).containsExactly(commentDto3, commentDto2, commentDto1);

        comments = adminCommentService.getComments(List.of(eventDto.getId() + 1), null, null, 0, 10);

        assertThat(comments).isEmpty();

        String encodedDate = UriUtils.encode(formatter.format(commentDto1.getCreated().minusHours(1)), StandardCharsets.UTF_8);
        comments = adminCommentService.getComments(null, encodedDate, null, 0, 10);

        assertThat(comments).hasSize(3).containsExactly(commentDto3, commentDto2, commentDto1);

        comments = adminCommentService.getComments(null, null, encodedDate, 0, 10);

        assertThat(comments).isEmpty();
    }

    @Test
    void testDeleteComment() {
        adminCommentService.deleteComment(commentDto1.getId());

        assertThrows(NotFoundException.class,
                () -> userCommentService.validateComment(commentDto1.getId()));
    }
}