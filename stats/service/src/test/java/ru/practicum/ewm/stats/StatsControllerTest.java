package ru.practicum.ewm.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.EndpointHitDto;
import dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatsController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StatsControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @MockBean
    private StatsService statsService;

    @Test
    void testSaveEndpointHit() throws Exception {
        EndpointHit endpointHit = new EndpointHit(1L, "a", "/a", "0.0.0.0",
                LocalDateTime.from(formatter.parse("2000-01-01 00:30:00")));
        EndpointHitDto endpointHitDto = new EndpointHitDto("a", "/a", "0.0.0.0",
                LocalDateTime.from(formatter.parse("2000-01-01 00:30:00")));

        when(statsService.saveEndpointHit(any()))
                .thenReturn(endpointHit);

        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(endpointHitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(statsService, times(1)).saveEndpointHit(endpointHitDto);
    }

    @Test
    void testGetViewStats() throws Exception {
        ViewStatsDto viewStatsDto = new ViewStatsDto("a", "/a", 1L);
        List<ViewStatsDto> viewStats = List.of(viewStatsDto);
        String start = UriUtils.encode("2000-01-01 00:30:00", StandardCharsets.UTF_8);
        String end = UriUtils.encode("2001-01-01 00:30:00", StandardCharsets.UTF_8);
        String uri = UriUtils.encode("/a", StandardCharsets.UTF_8);

        when(statsService.getViewStats(anyString(), anyString(), any(), anyBoolean()))
                .thenReturn(viewStats);

        mvc.perform(get("/stats?start=" + start +
                        "&end=" + end +
                        "&uris=" + uri +
                        "&unique=false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].app", is(viewStatsDto.getApp())))
                .andExpect(jsonPath("$.[0].uri", is(viewStatsDto.getUri())))
                .andExpect(jsonPath("$.[0].hits", is(viewStatsDto.getHits()), Long.class));

        verify(statsService, times(1))
                .getViewStats(start, end, List.of(uri), false);
    }
}