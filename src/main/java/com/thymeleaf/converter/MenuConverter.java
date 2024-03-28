package com.thymeleaf.converter;

import com.thymeleaf.dto.MenuDTO;
import com.thymeleaf.entity.MenuEntity;
import org.springframework.stereotype.Component;

@Component
public class MenuConverter {

    public MenuEntity toEntity(MenuDTO dto){
        MenuEntity entity = new MenuEntity();
        if (dto.getId() != null){
            entity.setId(dto.getId());
        }
        entity.setParentId(dto.getParentId());
        entity.setUrl(dto.getUrl());
        entity.setName(dto.getName());
        entity.setOrderIndex(dto.getOrderIndex());
        return entity;
    }

    public MenuEntity toEntity(MenuDTO dto, MenuEntity entity){
        if (dto.getId() != null){
            entity.setId(dto.getId());
        }
        entity.setParentId(dto.getParentId());
        entity.setUrl(dto.getUrl());
        entity.setName(dto.getName());
        entity.setOrderIndex(dto.getOrderIndex());
        entity.setActiveFlag(dto.getActiveFlag());
        return entity;
    }

    public MenuDTO toDTO(MenuEntity entity){
        MenuDTO dto = new MenuDTO();
        dto.setId(entity.getId());
        dto.setParentId(entity.getParentId());
        dto.setUrl(entity.getUrl());
        dto.setName(entity.getName());
        dto.setOrderIndex(entity.getOrderIndex());
        dto.setActiveFlag(entity.getActiveFlag());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setModifiedDate(entity.getModifiedDate());
        return dto;
    }
}
