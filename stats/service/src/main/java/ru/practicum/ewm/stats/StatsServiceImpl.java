package ru.practicum.ewm.stats;

import dto.EndpointHitDto;
import dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.exception.ValidationException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EndpointHit saveEndpointHit(EndpointHitDto endpointHitDto) {
        return statsRepository.save(EndpointHitMapper.mapToEndpointHit(endpointHitDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getViewStats(String start, String end, List<String> uris, boolean unique) {
        try {
            LocalDateTime startDate = LocalDateTime.parse(UriUtils.decode(start, StandardCharsets.UTF_8), formatter);
            LocalDateTime endDate = LocalDateTime.parse(UriUtils.decode(end, StandardCharsets.UTF_8), formatter);

            if (endDate.isBefore(startDate)) {
                throw new ValidationException("Конечная дата должна быть позже начальной");
            }
            List<String> decodedUris = uris.stream()
                    .map(uri -> UriUtils.decode(uri, StandardCharsets.UTF_8))
                    .toList();

            if (unique) {
                return statsRepository.getUniqueViewStats(startDate, endDate, decodedUris);
            } else {
                return statsRepository.getViewStats(startDate, endDate, decodedUris);
            }
        } catch (DateTimeParseException e) {
            throw new ValidationException("Некорректный формат даты");
        }
    }
}
