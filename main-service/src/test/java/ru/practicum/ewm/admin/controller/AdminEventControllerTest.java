package ru.practicum.ewm.admin.controller;

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
import ru.practicum.ewm.admin.service.AdminEventService;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.Location;
import ru.practicum.ewm.dto.event.UpdateEventDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.EventState;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminEventController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AdminEventControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private AdminEventService eventService;

    @Test
    void testGetEvents() throws Exception {
        LocalDateTime date = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0, 0).plusYears(1);
        EventFullDto eventFullDto = new EventFullDto(1L, "a", new CategoryDto(1L, "a"),
                List.of(), 2L, date, "b", date, new UserShortDto(1L, "a"),
                new Location((float) 10.0, (float) 20.0), true, 5, date,
                false, EventState.PENDING, "d", 10L);

        when(eventService.getEvents(List.of(1L, 2L), List.of(EventState.PUBLISHED), List.of(1L),
                "2000", "2001", 1, 3))
                .thenReturn(List.of(eventFullDto));

        String path = "/admin/events?users=1,2&states=PUBLISHED&categories=1"
                + "&rangeStart=2000&rangeEnd=2001&from=1&size=3";
        MvcResult result = mvc.perform(get(path)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        List<EventFullDto> eventList = mapper.readValue(json, new TypeReference<>() {});

        assertThat(eventList).hasSize(1).contains(eventFullDto);

        verify(eventService, times(1))
                .getEvents(List.of(1L, 2L), List.of(EventState.PUBLISHED), List.of(1L),
                        "2000", "2001", 1, 3);
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

        when(eventService.updateEvent(1L, updateEventDto))
                .thenReturn(eventFullDto);

        MvcResult result = mvc.perform(patch("/admin/events/1")
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
                .updateEvent(1L, updateEventDto);
    }
}