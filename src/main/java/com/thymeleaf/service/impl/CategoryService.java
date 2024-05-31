package com.thymeleaf.service.impl;

import com.thymeleaf.converter.CategoryConverter;
import com.thymeleaf.dto.CategoryDTO;
import com.thymeleaf.entity.CategoryEntity;
import com.thymeleaf.repository.ICategoryRepository;
import com.thymeleaf.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryConverter categoryConverter;

    private final ICategoryRepository categoryRepository;


    @Override
    public CategoryDTO save(CategoryDTO dto) {
        CategoryEntity categoryEntity = new CategoryEntity();
        if (dto.getId() != null) {
            Optional<CategoryEntity> optionalCategoryEntity = categoryRepository.findById(dto.getId());
            if (optionalCategoryEntity.isPresent()) {
                CategoryEntity oldCategoryEntity = optionalCategoryEntity.get();
                categoryEntity = categoryConverter.toEntity(dto, oldCategoryEntity);
            }
        } else {
            categoryEntity = categoryConverter.toEntity(dto);
        }
        categoryEntity = categoryRepository.save(categoryEntity);
        return categoryConverter.toDTO(categoryEntity);
    }

    @Override
    public void delete(Integer[] ids) {
        categoryRepository.deleteAllByIdInBatch(Arrays.asList(ids));
    }

    @Override
    public Page<CategoryDTO> findAll(Pageable pageable) {
        Page<CategoryEntity> entityPage = categoryRepository.findAll(pageable);
        List<CategoryDTO> dtoList = entityPage.stream().map(category -> {
            return categoryConverter.toDTO(category);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    public List<CategoryDTO> findAllByActiveFlag(Integer activeFlag) {
        List<CategoryEntity> list = categoryRepository.findAllByActiveFlag(activeFlag);
        return list.stream().map(category -> {
            return categoryConverter.toDTO(category);
        }).collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> findAll() {
        List<CategoryEntity> results = categoryRepository.findAll();
        return results.stream().map(category -> {
            return categoryConverter.toDTO(category);
        }).collect(Collectors.toList());
    }

    @Override
    public CategoryDTO findByCode(String code) {
        Optional<CategoryEntity> optional = categoryRepository.findByCode(code);
        return optional.map(categoryEntity -> categoryConverter.toDTO(categoryEntity)).orElse(null);
    }

    @Override
    public CategoryDTO findById(Integer id) {
        Optional<CategoryEntity> optional = categoryRepository.findById(id);
        return optional.map(categoryEntity -> categoryConverter.toDTO(categoryEntity)).orElse(null);
    }

    @Override
    public Page<CategoryDTO> findByNameContaining(String keyword, Pageable pageable) {
        Page<CategoryEntity> entityPage = categoryRepository.findByNameContaining(keyword, pageable);
        List<CategoryDTO> dtoList = entityPage.stream().map(category -> {
            return categoryConverter.toDTO(category);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }
}
