package ru.practicum.ewm.guest.service;

import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.model.Compilation;

import java.util.List;

public interface GuestCompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilation(Long compId);

    Compilation validateCompilation(Long compId);
}
