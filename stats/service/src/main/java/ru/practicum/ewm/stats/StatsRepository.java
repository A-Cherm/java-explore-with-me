package ru.practicum.ewm.stats;

import dto.ViewStatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("""
            select new dto.ViewStatsDto(eh.app, eh.uri, count(*) as c)
            from EndpointHit as eh
            where eh.uri in ?3
            and eh.timestamp between ?1 and ?2
            group by eh.app, eh.uri
            order by c desc
            """)
    List<ViewStatsDto> getViewStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("""
            select new dto.ViewStatsDto(eh.app, eh.uri, count(distinct eh.ip) as c)
            from EndpointHit as eh
            where eh.uri in ?3
            and eh.timestamp between ?1 and ?2
            group by eh.app, eh.uri
            order by c desc
            """)
    List<ViewStatsDto> getUniqueViewStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("""
            select new dto.ViewStatsDto(eh.app, eh.uri, count(*) as c)
            from EndpointHit as eh
            where eh.timestamp between ?1 and ?2
            group by eh.app, eh.uri
            order by c desc
            """)
    List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end);

    @Query("""
            select new dto.ViewStatsDto(eh.app, eh.uri, count(distinct eh.ip) as c)
            from EndpointHit as eh
            where eh.timestamp between ?1 and ?2
            group by eh.app, eh.uri
            order by c desc
            """)
    List<ViewStatsDto> getUniqueViewStats(LocalDateTime start, LocalDateTime end);
}
