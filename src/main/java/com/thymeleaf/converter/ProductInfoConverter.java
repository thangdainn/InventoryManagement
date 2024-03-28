package com.thymeleaf.converter;

import com.thymeleaf.dto.ProductInfoDTO;
import com.thymeleaf.entity.ProductInfoEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductInfoConverter {

    public ProductInfoEntity toEntity(ProductInfoDTO dto){
        ProductInfoEntity entity = new ProductInfoEntity();
        if (dto.getId() != null){
            entity.setId(dto.getId());
        }
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());
        entity.setImgUrl(dto.getImgUrl());
        return entity;
    }

    public ProductInfoEntity toEntity(ProductInfoDTO dto, ProductInfoEntity entity){
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        if (dto.getImgUrl() != null){
            entity.setImgUrl(dto.getImgUrl());
        }
        return entity;
    }

    public ProductInfoDTO toDTO(ProductInfoEntity entity){
        ProductInfoDTO dto = new ProductInfoDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setImgUrl(entity.getImgUrl());
        dto.setCategoryId(entity.getCategory().getId());
        dto.setActiveFlag(entity.getActiveFlag());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setModifiedDate(entity.getModifiedDate());
        return dto;
    }


}
