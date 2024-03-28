package com.thymeleaf.converter;

import com.thymeleaf.dto.RoleDTO;
import com.thymeleaf.entity.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleConverter {

    @Autowired
    private AuthConverter authConverter;

    public RoleEntity toEntity(RoleDTO dto){
        RoleEntity entity = new RoleEntity();
        if (dto.getId() != null){
            entity.setId(dto.getId());
        }
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    public RoleEntity toEntity(RoleDTO dto, RoleEntity entity){
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    public RoleDTO toDTO(RoleEntity entity){
        RoleDTO dto = new RoleDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setActiveFlag(entity.getActiveFlag());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setModifiedDate(entity.getModifiedDate());
        return dto;
    }
}
