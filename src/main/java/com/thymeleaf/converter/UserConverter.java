package com.thymeleaf.converter;

import com.thymeleaf.dto.RoleDTO;
import com.thymeleaf.dto.UserDTO;
import com.thymeleaf.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserConverter {

    @Autowired
    private RoleConverter roleConverter;

    public UserEntity toEntity(UserDTO dto){
        UserEntity entity = new UserEntity();
        if (dto.getId() != null){
            entity.setId(dto.getId());
        }
        entity.setUserName(dto.getUserName());
        entity.setPassword(dto.getPassword());
        entity.setName(dto.getName());
        return entity;
    }

    public UserEntity toEntity(UserDTO dto, UserEntity entity){
        entity.setUserName(dto.getUserName());
        entity.setPassword(dto.getPassword());
        entity.setName(dto.getName());
        return entity;
    }

    public UserDTO toDTO(UserEntity entity){
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUserName(entity.getUserName());
        dto.setPassword(entity.getPassword());
        dto.setName(entity.getName());
        List<RoleDTO> roles = entity.getRoles()
                .stream()
                .map(roleEntity -> {
                    return roleConverter.toDTO(roleEntity);
                })
                .collect(Collectors.toList());
        dto.setRoles(roles);
        dto.setActiveFlag(entity.getActiveFlag());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setModifiedDate(entity.getModifiedDate());
        return dto;
    }
}
