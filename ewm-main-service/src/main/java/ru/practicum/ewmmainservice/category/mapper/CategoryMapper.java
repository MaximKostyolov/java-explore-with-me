package ru.practicum.ewmmainservice.category.mapper;

import ru.practicum.ewmmainservice.category.model.CategoryDto;
import ru.practicum.ewmmainservice.category.model.NewCategoryDto;

public class CategoryMapper {

    public static CategoryDto newCategoryDtoToCategoryDto(NewCategoryDto newCategoryDto) {
        return CategoryDto.builder()
                .name(newCategoryDto.getName())
                .build();
    }

}