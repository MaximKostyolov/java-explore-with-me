package ru.practicum.ewmmainservice.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmmainservice.category.model.Category;

import java.util.List;

public interface CategoryJpaRepository extends JpaRepository<Category, Integer> {

    @Query(value = "select * from categories c " +
            "where c.name = ?1", nativeQuery = true)
    List<Category> findByName(String name);
}