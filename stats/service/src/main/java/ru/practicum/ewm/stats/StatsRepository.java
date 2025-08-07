package ru.practicum.ewm.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("""
            select new ru.practicum.ewm.stats.ViewStatsDto(eh.app, eh.uri, count(*))
            from EndpointHit as eh
            where eh.uri in ?3
            and eh.timestamp between ?1 and ?2
            group by eh.app, eh.uri
            """)
    List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("""
            select new ru.practicum.ewm.stats.ViewStatsDto(eh.app, eh.uri, count(distinct eh.ip))
            from EndpointHit as eh
            where eh.uri in ?3
            and eh.timestamp between ?1 and ?2
            group by eh.app, eh.uri
            """)
    List<ViewStatsDto> getUniqueViewStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
