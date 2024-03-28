package com.thymeleaf.converter;

import com.thymeleaf.dto.AuthDTO;
import com.thymeleaf.entity.AuthEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthConverter {

    public AuthEntity toEntity(AuthDTO dto){
        AuthEntity entity = new AuthEntity();
        if (dto.getId() != null){
            entity.setId(dto.getId());
        }
        entity.setPermission(dto.getPermission());
        return entity;
    }

    public AuthEntity toEntity(AuthDTO dto, AuthEntity entity){
        if (dto.getId() != null){
            entity.setId(dto.getId());
        }
        entity.setPermission(dto.getPermission());
        return entity;
    }

    public AuthDTO toDTO(AuthEntity entity){
        AuthDTO dto = new AuthDTO();
        dto.setId(entity.getId());
        dto.setPermission(entity.getPermission());
        dto.setRoleId(entity.getRole().getId());
        dto.setMenuId(entity.getMenu().getId());
        dto.setActiveFlag(entity.getActiveFlag());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setModifiedDate(entity.getModifiedDate());
        return dto;
    }
}
