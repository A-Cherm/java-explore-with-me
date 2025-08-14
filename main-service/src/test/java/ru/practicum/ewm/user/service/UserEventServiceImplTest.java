package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.ewm.admin.service.AdminCategoryService;
import ru.practicum.ewm.admin.service.AdminCategoryServiceImpl;
import ru.practicum.ewm.admin.service.UserService;
import ru.practicum.ewm.admin.service.UserServiceImpl;
import ru.practicum.ewm.config.QuerydslConfig;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.guest.service.GuestCategoryServiceImpl;
import ru.practicum.ewm.guest.service.GuestEventServiceImpl;
import ru.practicum.ewm.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({UserEventServiceImpl.class, GuestCategoryServiceImpl.class, UserServiceImpl.class,
        GuestEventServiceImpl.class, AdminCategoryServiceImpl.class, QuerydslConfig.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserEventServiceImplTest {
    private final UserEventService eventService;
    private final UserService userService;
    private final AdminCategoryService categoryService;

    private LocalDateTime date;
    private Location location;
    private UserDto userDto;
    private UserShortDto userShortDto;
    private CategoryDto categoryDto;
    private NewEventDto newEventDto;

    @BeforeEach
    void setup() {
        date = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0, 0).plusYears(1);
        location = new Location((float) 10.0, (float) 20.0);
        userDto = userService.createUser(new UserDto(null, "a", "a@mail"));
        categoryDto = categoryService.createCategory(new CategoryDto(null, "abc"));
        newEventDto = new NewEventDto("a".repeat(20), categoryDto.getId(), "b".repeat(20), date,
                location, true, 5, false, "c".repeat(5));

        userShortDto = new UserShortDto(userDto.getId(), userDto.getName());
    }

    @Test
    void testGetEvents() {
        eventService.createEvent(userDto.getId(), newEventDto);

        List<EventShortDto> events = eventService.getEvents(userDto.getId(), 0, 10);

        assertThat(events).isNotNull().hasSize(1);
        assertThat(events.getFirst())
                .hasFieldOrPropertyWithValue("annotation", newEventDto.getAnnotation())
                .hasFieldOrPropertyWithValue("confirmedRequests", 0L)
                .hasFieldOrPropertyWithValue("paid", newEventDto.getPaid())
                .hasFieldOrPropertyWithValue("category", categoryDto)
                .hasFieldOrPropertyWithValue("eventDate", date)
                .hasFieldOrPropertyWithValue("initiator", userShortDto)
                .hasFieldOrPropertyWithValue("views", 0L)
                .hasFieldOrPropertyWithValue("title", newEventDto.getTitle());
    }

    @Test
    void testCreateEvent() {
        EventFullDto createdEvent = eventService.createEvent(userDto.getId(), newEventDto);

        assertThat(createdEvent).isNotNull()
                .hasFieldOrPropertyWithValue("annotation", newEventDto.getAnnotation())
                .hasFieldOrPropertyWithValue("confirmedRequests", 0L)
                .hasFieldOrPropertyWithValue("paid", newEventDto.getPaid())
                .hasFieldOrPropertyWithValue("category", categoryDto)
                .hasFieldOrProperty("createdOn")
                .hasFieldOrPropertyWithValue("eventDate", date)
                .hasFieldOrPropertyWithValue("initiator", userShortDto)
                .hasFieldOrPropertyWithValue("location", location)
                .hasFieldOrPropertyWithValue("requestModeration", newEventDto.getRequestModeration())
                .hasFieldOrPropertyWithValue("participantLimit", newEventDto.getParticipantLimit())
                .hasFieldOrPropertyWithValue("state", EventState.PENDING)
                .hasFieldOrPropertyWithValue("views", 0L)
                .hasFieldOrPropertyWithValue("title", newEventDto.getTitle());
    }

    @Test
    void testGetEvent() {
        EventFullDto createdEvent = eventService.createEvent(userDto.getId(), newEventDto);
        EventFullDto event = eventService.getEvent(userDto.getId(), createdEvent.getId());

        assertThat(event).isNotNull()
                .hasFieldOrPropertyWithValue("annotation", newEventDto.getAnnotation())
                .hasFieldOrPropertyWithValue("confirmedRequests", 0L)
                .hasFieldOrPropertyWithValue("paid", newEventDto.getPaid())
                .hasFieldOrPropertyWithValue("category", categoryDto)
                .hasFieldOrProperty("createdOn")
                .hasFieldOrPropertyWithValue("eventDate", date)
                .hasFieldOrPropertyWithValue("initiator", userShortDto)
                .hasFieldOrPropertyWithValue("location", location)
                .hasFieldOrPropertyWithValue("requestModeration", newEventDto.getRequestModeration())
                .hasFieldOrPropertyWithValue("participantLimit", newEventDto.getParticipantLimit())
                .hasFieldOrPropertyWithValue("state", EventState.PENDING)
                .hasFieldOrPropertyWithValue("views", 0L)
                .hasFieldOrPropertyWithValue("title", newEventDto.getTitle());
    }

    @Test
    void testUpdateEvent() {
        EventFullDto createdEvent = eventService.createEvent(userDto.getId(), newEventDto);
        UpdateEventDto updateEventDto = new UpdateEventDto();
        updateEventDto.setParticipantLimit(50);
        updateEventDto.setStateAction(EventStateAction.CANCEL_REVIEW);

        EventFullDto event = eventService.updateEvent(userDto.getId(), createdEvent.getId(), updateEventDto);

        assertThat(event).isNotNull()
                .hasFieldOrPropertyWithValue("annotation", newEventDto.getAnnotation())
                .hasFieldOrPropertyWithValue("confirmedRequests", 0L)
                .hasFieldOrPropertyWithValue("paid", newEventDto.getPaid())
                .hasFieldOrPropertyWithValue("category", categoryDto)
                .hasFieldOrProperty("createdOn")
                .hasFieldOrPropertyWithValue("eventDate", date)
                .hasFieldOrPropertyWithValue("initiator", userShortDto)
                .hasFieldOrPropertyWithValue("location", location)
                .hasFieldOrPropertyWithValue("requestModeration", newEventDto.getRequestModeration())
                .hasFieldOrPropertyWithValue("participantLimit", 50)
                .hasFieldOrPropertyWithValue("state", EventState.CANCELED)
                .hasFieldOrPropertyWithValue("views", 0L)
                .hasFieldOrPropertyWithValue("title", newEventDto.getTitle());
    }
}