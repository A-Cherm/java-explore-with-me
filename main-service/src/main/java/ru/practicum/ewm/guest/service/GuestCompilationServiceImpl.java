package ru.practicum.ewm.guest.service;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.QCompilation;
import ru.practicum.ewm.repository.CompilationRepository;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GuestCompilationServiceImpl implements GuestCompilationService {
    private final CompilationRepository compilationRepository;
    private final GuestEventService guestEventService;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        QCompilation compilation = QCompilation.compilation;
        JPAQuery<Compilation> jpaQuery = queryFactory.selectFrom(compilation);

        if (pinned != null) {
            jpaQuery.where(compilation.pinned.eq(pinned));
        }
        jpaQuery.offset(from).limit(size);
        List<Compilation> compilations = jpaQuery.fetch();

        Set<Long> eventIds = new HashSet<>();
        compilations.forEach(compilation1 -> eventIds.addAll(compilation1.getEvents()));
        List<EventShortDto> events = guestEventService.getEventsForCompilation(eventIds);

        Map<Long, EventShortDto> eventMap = new HashMap<>();
        events.forEach(event -> eventMap.put(event.getId(), event));

        return compilations.stream()
                .map(compilation1 -> {
                    List<EventShortDto> compEvents = new ArrayList<>();
                    for (Long eventId : compilation1.getEvents()) {
                        compEvents.add(eventMap.get(eventId));
                    }
                    return CompilationMapper.mapToCompilationDto(compilation1, compEvents);
                })
                .toList();
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = validateCompilation(compId);
        List<EventShortDto> events = guestEventService.getEventsForCompilation(compilation.getEvents());

        return CompilationMapper.mapToCompilationDto(compilation, events);
    }

    @Override
    public Compilation validateCompilation(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Не найдена подборка событий",
                        "Нет подборки с id = " + compId));
    }
}
