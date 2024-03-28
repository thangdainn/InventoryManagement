package com.thymeleaf.converter;

import com.thymeleaf.dto.CategoryDTO;
import com.thymeleaf.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter {

    public CategoryEntity toEntity(CategoryDTO dto){
        CategoryEntity entity = new CategoryEntity();
        if (dto.getId() != null){
            entity.setId(dto.getId());
        }
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    public CategoryEntity toEntity(CategoryDTO dto, CategoryEntity entity){
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    public CategoryDTO toDTO(CategoryEntity entity){
        CategoryDTO dto = new CategoryDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setActiveFlag(entity.getActiveFlag());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setModifiedDate(entity.getModifiedDate());
        return dto;
    }


}
