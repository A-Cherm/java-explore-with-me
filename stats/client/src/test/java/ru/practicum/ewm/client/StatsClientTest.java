package ru.practicum.ewm.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ViewStatsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(StatsClient.class)
class StatsClientTest {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String apiUrl;

    private final MockRestServiceServer server;
    private final StatsClient statsClient;
    private final ObjectMapper mapper;

    @Autowired
    public StatsClientTest(@Value("${api.url}") String apiUrl, MockRestServiceServer server,
                           StatsClient statsClient, ObjectMapper mapper) {
        this.server = server;
        this.statsClient = statsClient;
        this.apiUrl = apiUrl;
        this.mapper = mapper;
        ReflectionTestUtils.setField(statsClient, "apiUrl", apiUrl);
    }

    @Test
    void testSaveEndpointHit() {
        server.expect(ExpectedCount.once(), requestTo(apiUrl + "/hit"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.CREATED));

        statsClient.saveEndpointHit("a", "/a", "0.0.0.0", LocalDateTime.now());

        server.verify();
    }

    @Test
    void testGetViewStats() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        String encodedStart = UriUtils.encode(formatter.format(now), StandardCharsets.UTF_8);
        String encodedEnd = UriUtils.encode(formatter.format(now.plusDays(1)), StandardCharsets.UTF_8);
        String uris = UriUtils.encode("/a", StandardCharsets.UTF_8) + ","
                + UriUtils.encode("/b", StandardCharsets.UTF_8);
        String url = apiUrl + "/stats?start=" + encodedStart
                + "&end=" + encodedEnd + "&uris=" + uris + "&unique=false";
        List<ViewStatsDto> viewStats = List.of(
                new ViewStatsDto("a", "/a", 1L),
                new ViewStatsDto("b", "/b", 2L));

        server.expect(ExpectedCount.once(), requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(mapper.writeValueAsString(viewStats), MediaType.APPLICATION_JSON));

        List<ViewStatsDto> response = statsClient.getViewStats(now, now.plusDays(1), List.of("/a", "/b"));

        server.verify();
        assertThat(response, notNullValue());
        assertThat(response.size(), equalTo(2));
        assertThat(response, hasItem(hasProperty("uri", is("/a"))));
        assertThat(response, hasItem(hasProperty("uri", is("/b"))));
    }
}