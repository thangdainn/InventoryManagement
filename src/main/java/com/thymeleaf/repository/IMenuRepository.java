package com.thymeleaf.repository;

import com.thymeleaf.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IMenuRepository extends JpaRepository<MenuEntity, Integer> {
    List<MenuEntity> findByActiveFlag(Integer active);
}
