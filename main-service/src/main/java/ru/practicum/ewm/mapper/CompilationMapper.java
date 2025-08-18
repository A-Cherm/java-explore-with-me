package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.model.Compilation;

import java.util.List;

public class CompilationMapper {
    public static CompilationDto mapToCompilationDto(Compilation compilation,
                                                     List<EventShortDto> events) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.isPinned(),
                events
        );
    }

    public static Compilation mapToCompilation(NewCompilationDto compilationDto) {
        return new Compilation(
                null,
                compilationDto.getTitle(),
                compilationDto.getPinned(),
                compilationDto.getEvents()
        );
    }
}
