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
import ru.practicum.ewmmainservice.compilations.dto.CompilationDto;
import ru.practicum.ewmmainservice.compilations.dto.NewCompilationDto;
import ru.practicum.ewmmainservice.compilations.dto.UpdateCompilationRequest;
import ru.practicum.ewmmainservice.compilations.repository.CompilationJpaRepository;
import ru.practicum.ewmmainservice.event.dto.EventShortDto;
import ru.practicum.ewmmainservice.event.mapper.EventMapper;
import ru.practicum.ewmmainservice.event.model.Event;
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
        List<Compilation> compilations;
        if (pinned) {
            compilations = compilationRepository.findAllPinned(page).getContent();
        } else {
            compilations = compilationRepository.findAllUnpinned(page).getContent();
        }
        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation compilation: compilations) {
            if (!compilation.getEvents().isEmpty()) {
                List<EventShortDto> events = EventMapper.toEventShortList(compilation.getEvents());
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
            List<EventShortDto> events = EventMapper.toEventShortList(compilation.getEvents());
            return CompilationMapper.toCompilationDto(compilation, events);
        } else {
            return CompilationMapper.toCompilationDto(compilation);
        }
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = eventService.getFullEventsByIds(newCompilationDto.getEvents());
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
        if (!events.isEmpty()) {
            List<EventShortDto> eventsShort = EventMapper.toEventShortList(events);
            return CompilationMapper.toCompilationDto(compilationRepository.save(compilation), eventsShort);
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
            List<Event> events = eventService.getFullEventsByIds(newCompilationDto.getEvents());
            compilation.setEvents(events);
        }
        if (newCompilationDto.getTitle() != null) {
            compilation.setTitle(newCompilationDto.getTitle());
        }
        if (newCompilationDto.isPinned()) {
            compilation.setPinned(newCompilationDto.isPinned());
        }
        compilationRepository.save(compilation);
        if (!compilation.getEvents().isEmpty()) {
            List<EventShortDto> events = EventMapper.toEventShortList(compilation.getEvents());
            return CompilationMapper.toCompilationDto(compilation, events);
        } else {
            return CompilationMapper.toCompilationDto(compilation);
        }
    }

}