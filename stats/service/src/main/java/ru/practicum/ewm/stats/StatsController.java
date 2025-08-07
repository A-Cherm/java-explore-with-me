package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveEndpointHit(@RequestBody EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = statsService.saveEndpointHit(endpointHitDto);
        log.info("Сохранён запрос {}", endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getViewStats(@RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam List<String> uris,
                                           @RequestParam(defaultValue = "false") boolean unique) {
        List<ViewStatsDto> viewStats = statsService.getViewStats(start, end, uris, unique);

        log.info("Возвращаются данные просмотров: {}", viewStats);
        return viewStats;
    }
}
