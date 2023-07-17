package ru.practicum.ewmmainservice.category.mapper;

import ru.practicum.ewmmainservice.category.model.Category;
import ru.practicum.ewmmainservice.category.dto.NewCategoryDto;

public class CategoryMapper {

    public static Category newCategoryDtoToCategoryDto(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

}