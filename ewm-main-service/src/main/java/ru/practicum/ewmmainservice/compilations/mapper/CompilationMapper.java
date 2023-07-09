package ru.practicum.ewmmainservice.compilations.mapper;

import ru.practicum.ewmmainservice.compilations.model.Compilation;
import ru.practicum.ewmmainservice.compilations.model.CompilationDto;
import ru.practicum.ewmmainservice.compilations.model.NewCompilationDto;
import ru.practicum.ewmmainservice.event.model.EventShortDto;

import java.util.List;

public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        StringBuilder events = new StringBuilder();
        if (newCompilationDto.getEvents().size() == 1) {
            events = new StringBuilder(newCompilationDto.getEvents().get(0).toString());
        } else if (newCompilationDto.getEvents().size() > 1)  {
            events = new StringBuilder(newCompilationDto.getEvents().get(0).toString());
            for (int i = 1; i < newCompilationDto.getEvents().size(); i++) {
                events.append(",").append(newCompilationDto.getEvents().get(i).toString());
            }
        }
        return Compilation.builder()
                .id(newCompilationDto.getId())
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.isPinned())
                .events(events.toString())
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