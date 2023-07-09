package ru.practicum.ewmmainservice.compilations.service;

import ru.practicum.ewmmainservice.compilations.model.CompilationDto;
import ru.practicum.ewmmainservice.compilations.model.NewCompilationDto;
import ru.practicum.ewmmainservice.compilations.model.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(boolean pinned, int from, int size);

    CompilationDto getCompilationById(int compId);

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(int compId);

    CompilationDto updateCompilation(int compId, UpdateCompilationRequest newCompilationDto);

}