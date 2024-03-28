package com.thymeleaf.service;

import com.thymeleaf.dto.MenuDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMenuService {
    MenuDTO findById(Integer id);
    Page<MenuDTO> findAll(Pageable pageable);
    List<MenuDTO> findByActiveFlag(Integer active);
    MenuDTO save(MenuDTO dto);
}
