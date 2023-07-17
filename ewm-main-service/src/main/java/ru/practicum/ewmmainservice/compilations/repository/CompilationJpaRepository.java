package ru.practicum.ewmmainservice.compilations.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmmainservice.compilations.model.Compilation;

public interface CompilationJpaRepository extends JpaRepository<Compilation, Integer> {

    @Query(value = "select c from Compilation c " +
            "where (c.pinned = true)")
    Page<Compilation> findAllPinned(Pageable page);

    @Query(value = "select c from Compilation c " +
            "where (c.pinned = false)")
    Page<Compilation> findAllUnpinned(Pageable page);
}
