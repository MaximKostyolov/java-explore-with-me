package ru.practicum.ewmmainservice.compilations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmainservice.compilations.model.Compilation;

public interface CompilationJpaRepository extends JpaRepository<Compilation, Integer> {
}
