package ru.practicum.ewm.stats;

import dto.EndpointHitDto;
import dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.exception.ValidationException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(StatsServiceImpl.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StatsServiceImplTest {
    private final StatsService statsService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    void testSaveEndpointHit() {
        EndpointHitDto endpointHitDto = new EndpointHitDto("asd", "/asd", "0.0.0.0", LocalDateTime.now());

        EndpointHit savedEndpoint = statsService.saveEndpointHit(endpointHitDto);

        assertEquals(endpointHitDto.getApp(), savedEndpoint.getApp());
        assertEquals(endpointHitDto.getUri(), savedEndpoint.getUri());
        assertEquals(endpointHitDto.getIp(), savedEndpoint.getIp());
        assertEquals(endpointHitDto.getTimestamp(), savedEndpoint.getTimestamp());
    }

    @Test
    void testGetViewStatsWithUris() {
        LocalDateTime now = LocalDateTime.now();
        EndpointHitDto endpointHitDto1 = new EndpointHitDto("a", "/a", "0.0.0.0", now);
        EndpointHitDto endpointHitDto2 = new EndpointHitDto("a", "/a", "0.0.0.0", now.plusDays(1));
        EndpointHitDto endpointHitDto3 = new EndpointHitDto("a", "/a", "1.0.0.0", now);
        EndpointHitDto endpointHitDto4 = new EndpointHitDto("a", "/b", "1.0.0.0", now);

        statsService.saveEndpointHit(endpointHitDto1);
        statsService.saveEndpointHit(endpointHitDto2);
        statsService.saveEndpointHit(endpointHitDto3);
        statsService.saveEndpointHit(endpointHitDto4);

        String start1 = UriUtils.encode(formatter.format(now.minusDays(1)), StandardCharsets.UTF_8);
        String end1 = UriUtils.encode(formatter.format(now.plusDays(2)), StandardCharsets.UTF_8);

        List<ViewStatsDto> viewStats = statsService.getViewStats(start1, end1, List.of("/a"), false);

        assertThat(viewStats.size(), equalTo(1));
        assertThat(viewStats.getFirst().getUri(), equalTo("/a"));
        assertThat(viewStats.getFirst().getHits(), equalTo(3L));

        String start2 = UriUtils.encode(formatter.format(now.plusHours(1)), StandardCharsets.UTF_8);

        viewStats = statsService.getViewStats(start2, end1, List.of("/a", "/b"), false);

        assertThat(viewStats.size(), equalTo(1));
        assertThat(viewStats.getFirst().getUri(), equalTo("/a"));
        assertThat(viewStats.getFirst().getHits(), equalTo(1L));

        viewStats = statsService.getViewStats(start1, end1, List.of("/a", "/b"), true);

        assertThat(viewStats.size(), equalTo(2));
        assertThat(viewStats.getFirst().getUri(), equalTo("/a"));
        assertThat(viewStats.getFirst().getHits(), equalTo(2L));
        assertThat(viewStats.getLast().getUri(), equalTo("/b"));
        assertThat(viewStats.getLast().getHits(), equalTo(1L));
    }

    @Test
    void testGetViewStatsWithoutUris() {
        LocalDateTime now = LocalDateTime.now();
        EndpointHitDto endpointHitDto1 = new EndpointHitDto("a", "/a", "0.0.0.0", now);
        EndpointHitDto endpointHitDto2 = new EndpointHitDto("a", "/a", "0.0.0.0", now.plusDays(1));
        EndpointHitDto endpointHitDto3 = new EndpointHitDto("a", "/a", "1.0.0.0", now);
        EndpointHitDto endpointHitDto4 = new EndpointHitDto("a", "/b", "1.0.0.0", now);

        statsService.saveEndpointHit(endpointHitDto1);
        statsService.saveEndpointHit(endpointHitDto2);
        statsService.saveEndpointHit(endpointHitDto3);
        statsService.saveEndpointHit(endpointHitDto4);

        String start1 = UriUtils.encode(formatter.format(now.minusDays(1)), StandardCharsets.UTF_8);
        String end1 = UriUtils.encode(formatter.format(now.plusDays(2)), StandardCharsets.UTF_8);

        List<ViewStatsDto> viewStats = statsService.getViewStats(start1, end1, null, false);

        assertThat(viewStats.size(), equalTo(2));
        assertThat(viewStats.getFirst().getUri(), equalTo("/a"));
        assertThat(viewStats.getFirst().getHits(), equalTo(3L));
        assertThat(viewStats.getLast().getUri(), equalTo("/b"));
        assertThat(viewStats.getLast().getHits(), equalTo(1L));

        String start2 = UriUtils.encode(formatter.format(now.plusHours(1)), StandardCharsets.UTF_8);

        viewStats = statsService.getViewStats(start2, end1, null, false);

        assertThat(viewStats.size(), equalTo(1));
        assertThat(viewStats.getFirst().getUri(), equalTo("/a"));
        assertThat(viewStats.getFirst().getHits(), equalTo(1L));

        viewStats = statsService.getViewStats(start1, end1, null, true);

        assertThat(viewStats.size(), equalTo(2));
        assertThat(viewStats.getFirst().getUri(), equalTo("/a"));
        assertThat(viewStats.getFirst().getHits(), equalTo(2L));
        assertThat(viewStats.getLast().getUri(), equalTo("/b"));
        assertThat(viewStats.getLast().getHits(), equalTo(1L));
    }

    @Test
    void testGetViewStatsWithInvalidDates() {
        LocalDateTime now = LocalDateTime.now();
        String start1 = UriUtils.encode(formatter.format(now.plusDays(1)), StandardCharsets.UTF_8);
        String end1 = UriUtils.encode(formatter.format(now.minusDays(2)), StandardCharsets.UTF_8);

        assertThrows(ValidationException.class,
                () -> statsService.getViewStats(start1, end1, List.of("/a"), false));
        assertThrows(ValidationException.class,
                () -> statsService.getViewStats("a", end1, List.of("/a"), false));
        assertThrows(ValidationException.class,
                () -> statsService.getViewStats(start1, "b", List.of("/a"), false));
    }
}