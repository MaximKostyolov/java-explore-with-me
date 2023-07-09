package ru.practicum.ewmmainservice.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmmainservice.category.model.CategoryDto;

import java.util.List;

public interface CategoryJpaRepository extends JpaRepository<CategoryDto, Integer> {

    @Query(value = "select * from categories c " +
            "where c.name = ?1", nativeQuery = true)
    List<CategoryDto> findByName(String name);
}