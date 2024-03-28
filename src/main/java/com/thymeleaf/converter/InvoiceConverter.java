package com.thymeleaf.converter;

import com.thymeleaf.dto.InvoiceDTO;
import com.thymeleaf.entity.InvoiceEntity;
import org.springframework.stereotype.Component;

@Component
public class InvoiceConverter {

    public InvoiceEntity toEntity(InvoiceDTO dto){
        InvoiceEntity entity = new InvoiceEntity();
        if (dto.getId() != null){
            entity.setId(dto.getId());
        }
        entity.setCode(dto.getCode());
        entity.setPrice(dto.getPrice());
        entity.setQty(dto.getQty());
        entity.setType(dto.getType());
        return entity;
    }

    public InvoiceEntity toEntity(InvoiceDTO dto, InvoiceEntity entity){
        entity.setCode(dto.getCode());
        entity.setPrice(dto.getPrice());
        entity.setQty(dto.getQty());
        return entity;
    }

    public InvoiceDTO toDTO(InvoiceEntity entity){
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProduct().getId());
        dto.setCode(entity.getCode());
        dto.setQty(entity.getQty());
        dto.setPrice(entity.getPrice());
        dto.setType(entity.getType());
        dto.setActiveFlag(entity.getActiveFlag());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setModifiedDate(entity.getModifiedDate());
        return dto;
    }


}
