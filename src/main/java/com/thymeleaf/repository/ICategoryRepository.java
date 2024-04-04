package com.thymeleaf.repository;

import com.thymeleaf.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ICategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    List<CategoryEntity> findByNameContaining(String keyword);
    Page<CategoryEntity> findByNameContaining(String keyword, Pageable pageable);
    Optional<CategoryEntity> findByCode(String code);

    List<CategoryEntity> findAllByActiveFlag(Integer activeFlag);
}
