package com.thymeleaf.service;

import com.thymeleaf.dto.CategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoryService {
    CategoryDTO save(CategoryDTO dto);
    void delete(Integer[] ids);
    Page<CategoryDTO> findAll(Pageable pageable);
    List<CategoryDTO> findAll();
    List<CategoryDTO> findAllByActiveFlag(Integer activeFlag);
    CategoryDTO findByCode(String code);
    CategoryDTO findById(Integer id);
    Page<CategoryDTO> findByNameContaining(String keyword, Pageable pageable);
}
