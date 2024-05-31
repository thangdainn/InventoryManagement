package com.thymeleaf.service.impl;

import com.thymeleaf.converter.MenuConverter;
import com.thymeleaf.dto.MenuDTO;
import com.thymeleaf.entity.MenuEntity;
import com.thymeleaf.repository.IMenuRepository;
import com.thymeleaf.service.IMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService implements IMenuService {

    private final IMenuRepository menuRepository;

    private final MenuConverter menuConverter;

    @Override
    public MenuDTO findById(Integer id) {
        Optional<MenuEntity> menuEntity = menuRepository.findById(id);
        if (menuEntity.isPresent()){
            return menuConverter.toDTO(menuEntity.get());
        }
        return null;
    }

    @Override
    public Page<MenuDTO> findAll(Pageable pageable) {
        Page<MenuEntity> entityPage = menuRepository.findAll(pageable);
        List<MenuDTO> menuDTOS = entityPage.stream().map(menu -> {
            return menuConverter.toDTO(menu);
        }).collect(Collectors.toList());
        return new PageImpl<>(menuDTOS, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    public List<MenuDTO> findByActiveFlag(Integer active) {
        List<MenuEntity> entities = menuRepository.findByActiveFlag(active);
        return entities.stream().map(menu -> {
            return menuConverter.toDTO(menu);
        }).collect(Collectors.toList());
    }

    @Override
    public MenuDTO save(MenuDTO dto) {
        MenuEntity menuEntity = new MenuEntity();
        if (dto.getId() != null) {
            Optional<MenuEntity> optional = menuRepository.findById(dto.getId());
            if (optional.isPresent()) {
                MenuEntity old = optional.get();
                menuEntity = menuConverter.toEntity(dto, old);
            }
        } else {
            menuEntity = menuConverter.toEntity(dto);
        }
        menuEntity = menuRepository.save(menuEntity);
        return menuConverter.toDTO(menuEntity);
    }
}
