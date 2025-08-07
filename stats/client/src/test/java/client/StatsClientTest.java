package client;

import dto.EndpointHitDto;
import dto.ViewStatsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsClientTest {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private StatsClient statsClient;

    @Test
    void testSaveEndpointHit() {
        EndpointHitDto endpointHitDto = new EndpointHitDto("a", "/a", "0.0.0.0", LocalDateTime.now());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<EndpointHitDto> httpEntity = new HttpEntity<>(endpointHitDto, headers);

        when(restTemplate.exchange("http://localhost:9090/hit", HttpMethod.POST, httpEntity, void.class))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.CREATED));

        statsClient.saveEndpointHit("a", "/a", "0.0.0.0", endpointHitDto.getTimestamp());

        verify(restTemplate, times(1))
                .exchange("http://localhost:9090/hit", HttpMethod.POST, httpEntity, void.class);
    }

    @Test
    void testGetViewStats() {
        LocalDateTime now = LocalDateTime.now();
        String encodedStart = UriUtils.encode(formatter.format(now), StandardCharsets.UTF_8);
        String encodedEnd = UriUtils.encode(formatter.format(now.plusDays(1)), StandardCharsets.UTF_8);
        String uris = UriUtils.encode("/a", StandardCharsets.UTF_8) + ","
                + UriUtils.encode("/b", StandardCharsets.UTF_8);
        String url = "http://localhost:9090/stats?start=" + encodedStart
                + "&end=" + encodedEnd + "&uris=" + uris + "&unique=false";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Object> httpEntity = new HttpEntity<>(null, headers);
        List<ViewStatsDto> viewStats = List.of(
                new ViewStatsDto("a", "/a", 1L),
                new ViewStatsDto("b", "/b", 2L));
        ParameterizedTypeReference<List<ViewStatsDto>> typeRef = new ParameterizedTypeReference<>() {};

        when(restTemplate.exchange(url, HttpMethod.GET, httpEntity, typeRef))
                .thenReturn(new ResponseEntity<>(viewStats, HttpStatus.OK));

        List<ViewStatsDto> response = statsClient.getViewStats(now, now.plusDays(1), List.of("/a", "/b"));

        verify(restTemplate, times(1))
                .exchange(url, HttpMethod.GET, httpEntity, typeRef);
        assertThat(response, equalTo(viewStats));
    }
}