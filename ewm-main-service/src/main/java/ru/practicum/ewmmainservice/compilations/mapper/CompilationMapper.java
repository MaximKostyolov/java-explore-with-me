package ru.practicum.ewmmainservice.compilations.mapper;

import ru.practicum.ewmmainservice.compilations.model.Compilation;
import ru.practicum.ewmmainservice.compilations.dto.CompilationDto;
import ru.practicum.ewmmainservice.compilations.dto.NewCompilationDto;
import ru.practicum.ewmmainservice.event.dto.EventShortDto;
import ru.practicum.ewmmainservice.event.model.Event;

import java.util.List;

public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        return Compilation.builder()
                .id(newCompilationDto.getId())
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.isPinned())
                .events(events)
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(events.toArray(EventShortDto[]::new))
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .build();
    }

}