package ru.practicum.ewm.client;

import dto.EndpointHitDto;
import dto.ViewStatsDto;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class StatsClient {
    private final RestTemplate rest;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String apiUrl;

    public StatsClient(String apiUrl, RestTemplateBuilder builder) {
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        this.rest = builder
                .uriTemplateHandler(defaultUriBuilderFactory)
                .build();
        this.apiUrl = apiUrl;
    }

    public void saveEndpointHit(String app, String uri, String ip, LocalDateTime timestamp) {
        EndpointHitDto endpointHitDto = new EndpointHitDto(app, uri, ip, timestamp);
        HttpEntity<EndpointHitDto> httpEntity = new HttpEntity<>(endpointHitDto, defaultHeaders());

        rest.exchange(apiUrl + "/hit", HttpMethod.POST, httpEntity, void.class);
    }

    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return getViewStats(start, end, uris, false);
    }

    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String encodedStart = UriUtils.encode(formatter.format(start), StandardCharsets.UTF_8);
        String encodedEnd = UriUtils.encode(formatter.format(end), StandardCharsets.UTF_8);
        String encodedUris = uris.stream()
                .map(uri -> UriUtils.encode(uri, StandardCharsets.UTF_8))
                .collect(Collectors.joining(","));
        HttpEntity<Object> httpEntity = new HttpEntity<>(null, defaultHeaders());

        ResponseEntity<List<ViewStatsDto>> response = rest.exchange(apiUrl + "/stats?start=" + encodedStart +
                        "&end=" + encodedEnd + "&uris=" + encodedUris + "&unique=" + unique,
                HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {});

        return response.getBody();
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return headers;
    }
}
