package ru.practicum.ewm.guest.controller;

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
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.guest.service.GuestEventService;
import ru.practicum.ewm.model.EventState;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GuestEventController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GuestEventControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private GuestEventService eventService;
    @MockBean
    private StatsClient statsClient;

    @Test
    void testGetEvents() throws Exception {
        LocalDateTime date = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0, 0).plusYears(1);
        EventShortDto eventShortDto = new EventShortDto(1L, "a",
                new CategoryDto(1L, "b"), 1L, date,
                new UserShortDto(1L, "c"), true, "d", 2L);

        when(eventService.getEvents("a", List.of(1L), true, "2000", "2001",
                true, "sort", 1, 3))
                .thenReturn(List.of(eventShortDto));

        String path = "/events?text=a&categories=1&paid=true&rangeStart=2000&rangeEnd=2001"
                + "&onlyAvailable=true&sort=sort&from=1&size=3";
        MvcResult result = mvc.perform(get(path)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        List<EventShortDto> eventList = mapper.readValue(json, new TypeReference<>() {});

        assertThat(eventList).hasSize(1).contains(eventShortDto);

        verify(eventService, times(1))
                .getEvents("a", List.of(1L), true, "2000", "2001",
                        true, "sort", 1, 3);
    }

    @Test
    void testGetEvent() throws Exception {
        LocalDateTime date = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0, 0).plusYears(1);
        EventFullDto eventFullDto = new EventFullDto(1L, "a", new CategoryDto(1L, "a"),
                2L, date, "b", date, new UserShortDto(1L, "a"),
                new Location((float) 10.0, (float) 20.0), true, 5, date,
                false, EventState.PENDING, "d", 10L);

        when(eventService.getEvent(1L))
                .thenReturn(eventFullDto);

        MvcResult result = mvc.perform(get("/events/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        EventFullDto returnedEvent = mapper.readValue(json, EventFullDto.class);

        assertThat(returnedEvent).isNotNull().isEqualTo(eventFullDto);

        verify(eventService, times(1))
                .getEvent(1L);
    }
}