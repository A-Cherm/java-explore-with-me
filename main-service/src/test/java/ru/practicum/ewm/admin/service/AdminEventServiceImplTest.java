package ru.practicum.ewm.admin.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.config.QuerydslConfig;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.guest.service.GuestCategoryServiceImpl;
import ru.practicum.ewm.guest.service.GuestEventServiceImpl;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.user.service.UserEventService;
import ru.practicum.ewm.user.service.UserEventServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({GuestEventServiceImpl.class, GuestCategoryServiceImpl.class, QuerydslConfig.class,
        UserServiceImpl.class, AdminCategoryServiceImpl.class, AdminEventServiceImpl.class,
        UserEventServiceImpl.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AdminEventServiceImplTest {
    private final AdminEventService adminEventService;
    private final UserService userService;
    private final AdminCategoryService categoryService;
    private final UserEventService userEventService;
    @MockBean
    private StatsClient statsClient;

    private LocalDateTime date;
    private UserDto userDto1;
    private UserDto userDto2;
    private CategoryDto categoryDto1;
    private CategoryDto categoryDto2;
    private EventFullDto eventFullDto1;
    private EventFullDto eventFullDto2;

    @BeforeEach
    void setUp() {
        date = LocalDateTime.now().plusYears(1);
        userDto1 = userService.createUser(new NewUserDto("aaa", "a@mail"));
        userDto2 = userService.createUser(new NewUserDto("bbb", "b@mail"));
        categoryDto1 = categoryService.createCategory(new CategoryDto(null, "abc"));
        categoryDto2 = categoryService.createCategory(new CategoryDto(null, "asd"));
        NewEventDto newEventDto1 = new NewEventDto("a".repeat(20), categoryDto1.getId(), "b".repeat(20),
                LocalDateTime.of(date.getYear(), 1, 1, 0, 0, 0),
                new Location((float) 10.0, (float) 10.0), true, 0, true, "ddd");
        NewEventDto newEventDto2 = new NewEventDto("a".repeat(20), categoryDto2.getId(), "b".repeat(20),
                LocalDateTime.of(date.getYear(), 2, 2, 0, 0, 0),
                new Location((float) 10.0, (float) 10.0), false, 10, false, "ddd");
        eventFullDto1 = userEventService.createEvent(userDto1.getId(), newEventDto1);
        eventFullDto2 = userEventService.createEvent(userDto2.getId(), newEventDto2);
    }

    @Test
    void testGetEvents() {
        UpdateEventDto updateEventDto = new UpdateEventDto();
        updateEventDto.setStateAction(EventStateAction.PUBLISH_EVENT);
        eventFullDto2 = adminEventService.updateEvent(eventFullDto2.getId(), updateEventDto);

        List<EventFullDto> events = adminEventService.getEvents(null, null, null,
                null, null, 0, 2);

        assertThat(events).hasSize(2).contains(eventFullDto1, eventFullDto2);

        events = adminEventService.getEvents(List.of(userDto1.getId()), null, null,
                null, null, 0, 2);

        assertThat(events).hasSize(1).contains(eventFullDto1);

        events = adminEventService.getEvents(null, null, List.of(categoryDto2.getId()),
                null, null, 0, 2);

        assertThat(events).hasSize(1).contains(eventFullDto2);

        events = adminEventService.getEvents(null, null, List.of(categoryDto2.getId()),
                null, null, 0, 2);

        assertThat(events).hasSize(1).contains(eventFullDto2);

        events = adminEventService.getEvents(null, List.of(EventState.PUBLISHED), null,
                null, null, 0, 2);

        assertThat(events).hasSize(1).contains(eventFullDto2);

        events = adminEventService.getEvents(null, List.of(EventState.PUBLISHED), null,
                null, null, 0, 2);

        assertThat(events).hasSize(1).contains(eventFullDto2);
    }

    @Test
    void testUpdateEvent() {
        UpdateEventDto updateEventDto = new UpdateEventDto();
        updateEventDto.setStateAction(EventStateAction.PUBLISH_EVENT);
        EventFullDto updatedEvent = adminEventService.updateEvent(eventFullDto2.getId(), updateEventDto);

        assertThat(updatedEvent)
                .hasFieldOrPropertyWithValue("state", EventState.PUBLISHED)
                .usingRecursiveComparison()
                .ignoringFields("state", "publishedOn")
                .isEqualTo(eventFullDto2);

        updateEventDto = new UpdateEventDto("f".repeat(20), categoryDto1.getId(), "g".repeat(20),
                date.plusYears(2), new Location((float) -10.0, (float) -10.0),
                true, 20, true, null, "hhh");
        updatedEvent = adminEventService.updateEvent(eventFullDto2.getId(), updateEventDto);

        assertThat(updatedEvent)
                .hasFieldOrPropertyWithValue("state", EventState.PUBLISHED)
                .hasFieldOrPropertyWithValue("annotation", updateEventDto.getAnnotation())
                .hasFieldOrPropertyWithValue("category", categoryDto1)
                .hasFieldOrPropertyWithValue("description", updateEventDto.getDescription())
                .hasFieldOrPropertyWithValue("eventDate", updateEventDto.getEventDate())
                .hasFieldOrPropertyWithValue("location", updateEventDto.getLocation())
                .hasFieldOrPropertyWithValue("paid", updateEventDto.getPaid())
                .hasFieldOrPropertyWithValue("participantLimit", updateEventDto.getParticipantLimit())
                .hasFieldOrPropertyWithValue("requestModeration", updateEventDto.getRequestModeration())
                .hasFieldOrPropertyWithValue("title", updateEventDto.getTitle());
    }
}