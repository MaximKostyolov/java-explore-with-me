package ru.practicum.ewmmainservice.category.service;

import ru.practicum.ewmmainservice.category.model.CategoryDto;
import ru.practicum.ewmmainservice.category.model.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto category);

    void removeCategory(int categoryId);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(int categoryId);

    CategoryDto updateCategory(int categoryId, CategoryDto updatedCategory);

}