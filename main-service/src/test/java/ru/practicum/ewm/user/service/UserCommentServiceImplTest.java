package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.practicum.ewm.admin.service.*;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.config.QuerydslConfig;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.user.NewUserDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.guest.service.GuestCategoryServiceImpl;
import ru.practicum.ewm.guest.service.GuestEventServiceImpl;
import ru.practicum.ewm.model.Comment;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({UserCommentServiceImpl.class, UserServiceImpl.class, AdminCategoryServiceImpl.class,
        GuestEventServiceImpl.class, UserEventServiceImpl.class, AdminEventServiceImpl.class,
        GuestCategoryServiceImpl.class, QuerydslConfig.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserCommentServiceImplTest {
    private final UserCommentService commentService;
    private final UserService userService;
    private final AdminCategoryService categoryService;
    private final UserEventService userEventService;
    private final AdminEventService adminEventService;
    @MockBean
    private StatsClient statsClient;

    private UserDto userDto1;
    private UserDto userDto2;
    private UserShortDto userShortDto1;
    private EventFullDto eventDto;

    @BeforeEach
    void setUp() {
        LocalDateTime date = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0, 0).plusYears(1);
        Location location = new Location((float) 10.0, (float) 20.0);
        userDto1 = userService.createUser(new NewUserDto("a", "a@mail"));
        userDto2 = userService.createUser(new NewUserDto("b", "b@mail"));
        userShortDto1 = new UserShortDto(userDto1.getId(), userDto1.getName());
        CategoryDto categoryDto = categoryService.createCategory(new CategoryDto(null, "abc"));
        eventDto = userEventService.createEvent(userDto1.getId(),
                new NewEventDto("a".repeat(20), categoryDto.getId(), "b".repeat(20), date,
                location, true, 5, false, "c".repeat(5)));
        UpdateEventDto updateEventDto = new UpdateEventDto();
        updateEventDto.setStateAction(EventStateAction.PUBLISH_EVENT);
        adminEventService.updateEvent(eventDto.getId(), updateEventDto);
    }

    @Test
    void testCreateComment() {
        NewCommentDto newCommentDto = new NewCommentDto("aaa");
        CommentDto commentDto = commentService.createComment(userDto1.getId(), eventDto.getId(), newCommentDto);

        assertThat(commentDto)
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("author", userShortDto1)
                .hasFieldOrPropertyWithValue("text", commentDto.getText())
                .hasFieldOrProperty("created");
    }

    @Test
    void testUpdateComment() {
        NewCommentDto newCommentDto = new NewCommentDto("aaa");
        CommentDto createdComment = commentService.createComment(userDto1.getId(), eventDto.getId(), newCommentDto);
        UpdateCommentDto updateCommentDto = new UpdateCommentDto("bbb");
        CommentDto updatedComment = commentService.updateComment(userDto1.getId(),
                createdComment.getId(), updateCommentDto);

        assertThat(updatedComment)
                .hasFieldOrPropertyWithValue("id", createdComment.getId())
                .hasFieldOrPropertyWithValue("author", userShortDto1)
                .hasFieldOrPropertyWithValue("text", updatedComment.getText())
                .hasFieldOrPropertyWithValue("created", createdComment.getCreated());
    }

    @Test
    void testDeleteComment() {
        NewCommentDto newCommentDto = new NewCommentDto("aaa");
        CommentDto commentDto = commentService.createComment(userDto1.getId(), eventDto.getId(), newCommentDto);

        commentService.deleteComment(userDto1.getId(), commentDto.getId());

        assertThrows(NotFoundException.class,
                () -> commentService.validateComment(commentDto.getId()));
    }

    @Test
    void testValidateComment() {
        NewCommentDto newCommentDto = new NewCommentDto("aaa");
        CommentDto commentDto = commentService.createComment(userDto1.getId(), eventDto.getId(), newCommentDto);

        Comment comment = commentService.validateComment(commentDto.getId());

        assertThat(comment)
                .hasFieldOrPropertyWithValue("id", commentDto.getId())
                .hasFieldOrPropertyWithValue("eventId", eventDto.getId())
                .hasFieldOrPropertyWithValue("text", commentDto.getText())
                .hasFieldOrPropertyWithValue("created", commentDto.getCreated());

        assertThrows(NotFoundException.class,
                () -> commentService.validateComment(commentDto.getId() + 1));
    }
}