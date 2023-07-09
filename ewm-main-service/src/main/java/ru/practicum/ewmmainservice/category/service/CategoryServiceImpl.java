package ru.practicum.ewmmainservice.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmainservice.category.mapper.CategoryMapper;
import ru.practicum.ewmmainservice.category.model.CategoryDto;
import ru.practicum.ewmmainservice.category.model.NewCategoryDto;
import ru.practicum.ewmmainservice.category.repository.CategoryJpaRepository;
import ru.practicum.ewmmainservice.event.repository.EventJpaRepository;
import ru.practicum.ewmmainservice.exception.AccesErrorException;
import ru.practicum.ewmmainservice.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryJpaRepository categoryRepository;

    private final EventJpaRepository eventRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto category) {
        try {
            return categoryRepository.save(CategoryMapper.newCategoryDtoToCategoryDto(category));
        } catch (RuntimeException exception) {
            throw new AccesErrorException("Category with name: " + category.getName() + " is already exsist.");
        }
    }

    @Override
    public void removeCategory(int categoryId) {
        CategoryDto categoryDto = categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("Category was not found"));
        if (eventRepository.findByCategoryId(categoryId).isEmpty()) {
            categoryRepository.deleteById(categoryId);
        } else {
            throw new AccesErrorException("Couldn't delete category.");
        }
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable page = PageRequest.of(from, size,
                Sort.by(Sort.Direction.DESC, "id"));
        return categoryRepository.findAll(page).getContent();
    }

    @Override
    public CategoryDto getCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category with id=" +
                categoryId + " was not found"));
    }

    @Override
    public CategoryDto updateCategory(int categoryId, CategoryDto updatedCategory) {
        CategoryDto categoryDto = categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("Categoty was not found."));
        if (updatedCategory.getName().equals(categoryDto.getName())) {
            return categoryDto;
        }
        List<CategoryDto> categories = categoryRepository.findByName(updatedCategory.getName());
        if (categories.isEmpty()) {
            categoryDto.setName(updatedCategory.getName());
            return categoryRepository.save(categoryDto);
        } else {
            throw new AccesErrorException("Category with name: " + updatedCategory.getName() + " is already exsist.");
        }
    }

}