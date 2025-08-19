package ru.practicum.ewm.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.user.service.UserEventService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserEventController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserEventControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private UserEventService eventService;

    @Test
    void testGetEvents() throws Exception {
        LocalDateTime date = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0, 0).plusYears(1);
        EventShortDto eventShortDto = new EventShortDto(1L, "a",
                new CategoryDto(1L, "b"), 1L, date,
                new UserShortDto(1L, "c"), true, "d", 2L);

        when(eventService.getEvents(1L, 2, 5))
                .thenReturn(List.of(eventShortDto));

        MvcResult result = mvc.perform(get("/users/1/events?from=2&size=5")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        List<EventShortDto> eventList = mapper.readValue(json, new TypeReference<>() {});

        assertThat(eventList).hasSize(1).contains(eventShortDto);

        verify(eventService, times(1))
                .getEvents(1L, 2, 5);
    }

    @Test
    void testCreateEvent() throws Exception {
        LocalDateTime date = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0, 0).plusYears(1);
        NewEventDto newEventDto = new NewEventDto("a".repeat(20), 1L, "b".repeat(20), date,
                new Location((float) 10.0, (float) 20.0), true, 5, false, "c".repeat(5));
        EventFullDto eventFullDto = new EventFullDto(1L, "a", new CategoryDto(1L, "a"),
                List.of(), 2L, date, "b", date, new UserShortDto(1L, "a"),
                new Location((float) 10.0, (float) 20.0), true, 5, date,
                false, EventState.PENDING, "d", 10L);

        when(eventService.createEvent(1L, newEventDto))
                .thenReturn(eventFullDto);

        MvcResult result = mvc.perform(post("/users/1/events")
                        .content(mapper.writeValueAsString(newEventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        EventFullDto createdEvent = mapper.readValue(json, EventFullDto.class);

        assertThat(createdEvent).isNotNull().isEqualTo(eventFullDto);

        verify(eventService, times(1))
                .createEvent(1L, newEventDto);
    }

    @Test
    void testGetEvent() throws Exception {
        LocalDateTime date = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0, 0).plusYears(1);
        EventFullDto eventFullDto = new EventFullDto(1L, "a", new CategoryDto(1L, "a"),
                List.of(), 2L, date, "b", date, new UserShortDto(1L, "a"),
                new Location((float) 10.0, (float) 20.0), true, 5, date,
                false, EventState.PENDING, "d", 10L);

        when(eventService.getEvent(1L, 1L))
                .thenReturn(eventFullDto);

        MvcResult result = mvc.perform(get("/users/1/events/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        EventFullDto returnedEvent = mapper.readValue(json, EventFullDto.class);

        assertThat(returnedEvent).isNotNull().isEqualTo(eventFullDto);

        verify(eventService, times(1))
                .getEvent(1L, 1L);
    }

    @Test
    void testUpdateEvent() throws Exception {
        LocalDateTime date = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0, 0).plusYears(1);
        UpdateEventDto updateEventDto = new UpdateEventDto();
        updateEventDto.setTitle("c".repeat(10));
        EventFullDto eventFullDto = new EventFullDto(1L, "a", new CategoryDto(1L, "a"),
                List.of(), 2L, date, "b", date, new UserShortDto(1L, "a"),
                new Location((float) 10.0, (float) 20.0), true, 5, date,
                false, EventState.PENDING, "d", 10L);

        when(eventService.updateEvent(1L, 1L, updateEventDto))
                .thenReturn(eventFullDto);

        MvcResult result = mvc.perform(patch("/users/1/events/1")
                        .content(mapper.writeValueAsString(updateEventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        EventFullDto returnedEvent = mapper.readValue(json, EventFullDto.class);

        assertThat(returnedEvent).isNotNull().isEqualTo(eventFullDto);

        verify(eventService, times(1))
                .updateEvent(1L, 1L, updateEventDto);
    }
}