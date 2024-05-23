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

    public UserEntity toEntity(UserDTO dto){
        UserEntity entity = new UserEntity();
        if (dto.getId() != null){
            entity.setId(dto.getId());
        }
        entity.setUserName(dto.getUserName());
        entity.setPassword(dto.getPassword());
        entity.setName(dto.getName());
        entity.setProviderId(dto.getProviderId());
        return entity;
    }

    public UserEntity toEntity(UserDTO dto, UserEntity entity){
        entity.setUserName(dto.getUserName());
        entity.setPassword(dto.getPassword());
        entity.setName(dto.getName());
        entity.setProviderId(dto.getProviderId());
        return entity;
    }

    public UserDTO toDTO(UserEntity entity){
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUserName(entity.getUserName());
        dto.setPassword(entity.getPassword());
        dto.setName(entity.getName());
        dto.setProviderId(entity.getProviderId());
        dto.setRoleIds(entity.getRoles().stream()
                .map(role -> role.getId())
                .collect(Collectors.toList())
                .toArray(new Integer[0]));
        dto.setActiveFlag(entity.getActiveFlag());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setModifiedDate(entity.getModifiedDate());
        return dto;
    }
}
