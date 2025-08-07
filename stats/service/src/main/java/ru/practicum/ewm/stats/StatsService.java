package ru.practicum.ewm.stats;

import dto.EndpointHitDto;
import dto.ViewStatsDto;

import java.util.List;

public interface StatsService {
    EndpointHit saveEndpointHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getViewStats(String start, String end, List<String> uris, boolean unique);
}
