package ru.practicum.ewm.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationDto;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.guest.service.GuestCompilationService;
import ru.practicum.ewm.guest.service.GuestEventService;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.repository.CompilationRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final GuestCompilationService guestCompilationService;
    private final GuestEventService guestEventService;

    @Override
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = compilationRepository.save(CompilationMapper.mapToCompilation(compilationDto));
        List<EventShortDto> events = guestEventService.getEventsForCompilation(compilation.getEvents());

        return CompilationMapper.mapToCompilationDto(compilation, events);
    }

    @Override
    public void deleteCompilation(Long compId) {
        guestCompilationService.validateCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto compilationDto) {
        Compilation compilation = guestCompilationService.validateCompilation(compId);
        String title = compilationDto.getTitle();

        if (title != null && title.isBlank()) {
            throw new ValidationException("Некорректные данные запроса",
                    "Заголовок не может состоять из пробелов");
        }
        if (title != null) {
            compilation.setTitle(title);
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getEvents() != null) {
            compilation.setEvents(compilationDto.getEvents());
        }
        compilation = compilationRepository.save(compilation);
        List<EventShortDto> events = guestEventService.getEventsForCompilation(compilation.getEvents());

        return CompilationMapper.mapToCompilationDto(compilation, events);
    }
}
