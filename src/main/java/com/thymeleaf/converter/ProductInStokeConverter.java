package com.thymeleaf.converter;

import com.thymeleaf.dto.ProductInStokeDTO;
import com.thymeleaf.entity.ProductInStokeEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductInStokeConverter {

    public ProductInStokeEntity toEntity(ProductInStokeDTO dto){
        ProductInStokeEntity entity = new ProductInStokeEntity();
        if (dto.getId() != null){
            entity.setId(dto.getId());
        }
        entity.setPrice(dto.getPrice());
        entity.setQty(dto.getQty());
        return entity;
    }

    public ProductInStokeEntity toEntity(ProductInStokeDTO dto, ProductInStokeEntity entity){
        entity.setPrice(dto.getPrice());
        entity.setQty(dto.getQty());
        return entity;
    }

    public ProductInStokeDTO toDTO(ProductInStokeEntity entity){
        ProductInStokeDTO dto = new ProductInStokeDTO();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProduct().getId());
        dto.setQty(entity.getQty());
        dto.setPrice(entity.getPrice());
        dto.setActiveFlag(entity.getActiveFlag());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setModifiedDate(entity.getModifiedDate());
        return dto;
    }


}
