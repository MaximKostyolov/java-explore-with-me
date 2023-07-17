package ru.practicum.ewmmainservice.category.service;

import ru.practicum.ewmmainservice.category.model.Category;
import ru.practicum.ewmmainservice.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    Category createCategory(NewCategoryDto category);

    void removeCategory(int categoryId);

    List<Category> getCategories(int from, int size);

    Category getCategoryById(int categoryId);

    Category updateCategory(int categoryId, Category updatedCategory);

}