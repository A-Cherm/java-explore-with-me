package ru.practicum.ewm.guest.service;

import dto.ViewStatsDto;
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
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.user.service.UserEventService;
import ru.practicum.ewm.user.service.UserEventServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import({GuestEventServiceImpl.class, GuestCategoryServiceImpl.class, QuerydslConfig.class,
        UserServiceImpl.class, AdminCategoryServiceImpl.class, UserEventServiceImpl.class,
        AdminEventServiceImpl.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GuestEventServiceImplTest {
    private final GuestEventService guestEventService;
    private final UserService userService;
    private final AdminCategoryService categoryService;
    private final UserEventService userEventService;
    private final AdminEventService adminEventService;
    @MockBean
    private StatsClient statsClient;

    private LocalDateTime date;
    private UserDto userDto1;
    private UserDto userDto2;
    private CategoryDto categoryDto1;
    private CategoryDto categoryDto2;
    private EventFullDto eventFullDto1;
    private EventFullDto eventFullDto2;
    private EventShortDto eventShortDto1;
    private EventShortDto eventShortDto2;

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
        NewEventDto newEventDto2 = new NewEventDto("c".repeat(20), categoryDto2.getId(), "d".repeat(20),
                LocalDateTime.of(date.getYear(), 2, 2, 0, 0, 0),
                new Location((float) 10.0, (float) 10.0), false, 10, false, "eee");
        eventFullDto1 = userEventService.createEvent(userDto1.getId(), newEventDto1);
        eventFullDto2 = userEventService.createEvent(userDto2.getId(), newEventDto2);
        UpdateEventDto updateEventDto = new UpdateEventDto();
        updateEventDto.setStateAction(EventStateAction.PUBLISH_EVENT);
        eventFullDto1 = adminEventService.updateEvent(eventFullDto1.getId(), updateEventDto);
        eventFullDto2 = adminEventService.updateEvent(eventFullDto2.getId(), updateEventDto);
        eventShortDto1 = new EventShortDto(eventFullDto1.getId(), eventFullDto1.getAnnotation(),
                eventFullDto1.getCategory(), eventFullDto1.getConfirmedRequests(), eventFullDto1.getEventDate(),
                eventFullDto1.getInitiator(), eventFullDto1.getPaid(), eventFullDto1.getTitle(),
                eventFullDto1.getViews());
        eventShortDto2 = new EventShortDto(eventFullDto2.getId(), eventFullDto2.getAnnotation(),
                eventFullDto2.getCategory(), eventFullDto2.getConfirmedRequests(), eventFullDto2.getEventDate(),
                eventFullDto2.getInitiator(), eventFullDto2.getPaid(), eventFullDto2.getTitle(),
                eventFullDto2.getViews());
    }

    @Test
    void testGetEvents() {
        List<EventShortDto> events = guestEventService.getEvents(null, null, null, null,
                null, false, null, 0, 2);

        assertThat(events).hasSize(2).contains(eventShortDto1, eventShortDto2);

        events = guestEventService.getEvents("aaa", null, null, null,
                null, false, null, 0, 2);

        assertThat(events).hasSize(1).contains(eventShortDto1);

        events = guestEventService.getEvents("ccc", null, null, null,
                null, false, null, 0, 2);

        assertThat(events).hasSize(1).contains(eventShortDto2);

        events = guestEventService.getEvents(null, List.of(categoryDto1.getId()), null, null,
                null, false, null, 0, 2);

        assertThat(events).hasSize(1).contains(eventShortDto1);

        events = guestEventService.getEvents(null, null, true, null,
                null, false, null, 0, 2);

        assertThat(events).hasSize(1).contains(eventShortDto1);

        events = guestEventService.getEvents(null, null, null, null,
                null, true, null, 0, 2);

        assertThat(events).hasSize(2).contains(eventShortDto1, eventShortDto2);

        events = guestEventService.getEvents(null, null, null, null,
                null, true, "EVENT_DATE", 0, 2);

        assertThat(events).hasSize(2).containsExactly(eventShortDto1, eventShortDto2);
    }

    @Test
    void testGetEvent() {
        EventFullDto event = guestEventService.getEvent(eventFullDto1.getId());

        assertThat(event)
                .usingRecursiveComparison()
                .isEqualTo(eventFullDto1);
    }

    @Test
    void testGetEventsForCompilation() {
        List<EventShortDto> events = guestEventService.getEventsForCompilation(Set.of(eventFullDto1.getId()));

        assertThat(events).hasSize(1).contains(eventShortDto1);

        events = guestEventService.getEventsForCompilation(Set.of(eventFullDto1.getId(), eventFullDto2.getId()));

        assertThat(events).hasSize(2).contains(eventShortDto1, eventShortDto2);
    }

    @Test
    void testGetViewsForEvent() {
        String uri = "/events/" + eventFullDto1.getId();
        ViewStatsDto viewStats = new ViewStatsDto("asd", uri, 3L);

        when(statsClient.getViewStats(any(), any(), any(), anyBoolean()))
                .thenReturn(List.of(viewStats));

        Long views = guestEventService.getViewsForEvent(eventFullDto1.getId());

        assertThat(views).isEqualTo(3L);
    }

    @Test
    void testGetViewsForEvents() {
        String uri1 = "/events/" + eventFullDto1.getId();
        String uri2 = "/events/" + eventFullDto2.getId();
        ViewStatsDto viewStats1 = new ViewStatsDto("asd", uri1, 3L);
        ViewStatsDto viewStats2 = new ViewStatsDto("asd", uri2, 4L);

        when(statsClient.getViewStats(any(), any(), any(), anyBoolean()))
                .thenReturn(List.of(viewStats1, viewStats2));

        Map<Long, Long> views = guestEventService
                .getViewsForEvents(List.of(eventFullDto1.getId(), eventFullDto2.getId()));

        assertThat(views.get(eventFullDto1.getId())).isEqualTo(viewStats1.getHits());
        assertThat(views.get(eventFullDto2.getId())).isEqualTo(viewStats2.getHits());
    }

    @Test
    void testValidateEvent() {
        Event event = guestEventService.validateEvent(eventFullDto1.getId());

        assertThat(event.getId()).isEqualTo(eventFullDto1.getId());
        assertThat(event.getAnnotation()).isEqualTo(eventFullDto1.getAnnotation());

        assertThrows(NotFoundException.class,
                () -> guestEventService.validateEvent(eventFullDto2.getId() + 1));
    }
}