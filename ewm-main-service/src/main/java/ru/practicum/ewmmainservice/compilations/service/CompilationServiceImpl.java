package ru.practicum.ewmmainservice.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmainservice.compilations.mapper.CompilationMapper;
import ru.practicum.ewmmainservice.compilations.model.Compilation;
import ru.practicum.ewmmainservice.compilations.model.CompilationDto;
import ru.practicum.ewmmainservice.compilations.model.NewCompilationDto;
import ru.practicum.ewmmainservice.compilations.model.UpdateCompilationRequest;
import ru.practicum.ewmmainservice.compilations.repository.CompilationJpaRepository;
import ru.practicum.ewmmainservice.event.model.EventShortDto;
import ru.practicum.ewmmainservice.event.service.EventServiceImpl;
import ru.practicum.ewmmainservice.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationJpaRepository compilationRepository;

    private final EventServiceImpl eventService;

    @Override
    public List<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        Pageable page = PageRequest.of(from, size,
                Sort.by(Sort.Direction.DESC, "id"));
        List<Compilation> compilations = compilationRepository.findAll(page).getContent();
        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation compilation: compilations) {
            if (!compilation.getEvents().isEmpty()) {
                String[] eventsId = compilation.getEvents().split(",");
                List<EventShortDto> events = eventService.getEventsByIds(eventsId);
                compilationDtoList.add(CompilationMapper.toCompilationDto(compilation, events));
            } else {
                compilationDtoList.add(CompilationMapper.toCompilationDto(compilation));
            }
        }
        return compilationDtoList;
    }

    @Override
    public CompilationDto getCompilationById(int compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation with id=" +
                compId + " was not found"));
        if (!compilation.getEvents().isEmpty()) {
            String[] eventsId = compilation.getEvents().split(",");
            List<EventShortDto> events = eventService.getEventsByIds(eventsId);
            return CompilationMapper.toCompilationDto(compilation, events);
        } else {
            return CompilationMapper.toCompilationDto(compilation);
        }
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        if (!compilation.getEvents().isEmpty()) {
            String[] eventsId = compilation.getEvents().split(",");
            List<EventShortDto> events = eventService.getEventsByIds(eventsId);
            return CompilationMapper.toCompilationDto(compilationRepository.save(compilation), events);
        } else {
            return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
        }
    }

    @Override
    public void deleteCompilation(int compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation with id=" +
                compId + " was not found"));
        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto updateCompilation(int compId, UpdateCompilationRequest newCompilationDto) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation with id=" +
                compId + " was not found"));
        if (newCompilationDto.getEvents() != null) {
            if (newCompilationDto.getEvents().size() == 1) {
                compilation.setEvents(newCompilationDto.getEvents().get(0).toString());
            } else if (newCompilationDto.getEvents().size() > 1) {
                StringBuilder events = new StringBuilder(newCompilationDto.getEvents().get(0).toString());
                for (int i = 1; i < newCompilationDto.getEvents().size(); i++) {
                    events.append(",").append(newCompilationDto.getEvents().get(i).toString());
                }
                compilation.setEvents(String.valueOf(events));
            }
        }
        if (newCompilationDto.getTitle() != null) {
            compilation.setTitle(newCompilationDto.getTitle());
        }
        if (newCompilationDto.isPinned()) {
            compilation.setPinned(newCompilationDto.isPinned());
        }
        compilationRepository.save(compilation);
        if (!compilation.getEvents().isEmpty()) {
            String[] eventsId = compilation.getEvents().split(",");
            List<EventShortDto> events = eventService.getEventsByIds(eventsId);
            return CompilationMapper.toCompilationDto(compilation, events);
        } else {
            return CompilationMapper.toCompilationDto(compilation);
        }
    }

}