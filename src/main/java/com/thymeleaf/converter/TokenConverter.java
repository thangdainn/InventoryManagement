package com.thymeleaf.converter;

import com.thymeleaf.dto.TokenDTO;
import com.thymeleaf.entity.TokenEntity;
import org.springframework.stereotype.Component;


@Component
public class TokenConverter {


    public TokenEntity toEntity(TokenDTO dto){
        TokenEntity entity = new TokenEntity();
        if (dto.getId() != null){
            entity.setId(dto.getId());
        }
//        entity.setToken(dto.getToken());
        entity.setRefreshToken(dto.getRefreshToken());
//        entity.setExpirationDate(dto.getExpirationDate());
        entity.setRefreshTokenExpirationDate(dto.getRefreshTokenExpirationDate());
        return entity;
    }

    public TokenEntity toEntity(TokenDTO dto, TokenEntity entity){
//        entity.setToken(dto.getToken());
        entity.setRefreshTokenExpirationDate(dto.getRefreshTokenExpirationDate());
//        entity.setExpirationDate(dto.getExpirationDate());
        entity.setRefreshToken(dto.getRefreshToken());
        return entity;
    }

    public TokenDTO toDTO(TokenEntity entity){
        TokenDTO dto = new TokenDTO();
        dto.setId(entity.getId());
//        dto.setToken(entity.getToken());
        dto.setRefreshToken(entity.getRefreshToken());
//        dto.setExpirationDate(entity.getExpirationDate());
        dto.setRefreshTokenExpirationDate(entity.getRefreshTokenExpirationDate());
        dto.setUserId(entity.getUser().getId());

        dto.setActiveFlag(entity.getActiveFlag());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setModifiedDate(entity.getModifiedDate());
        return dto;
    }
}
