package com.thymeleaf.converter;

import com.thymeleaf.dto.HistoryDTO;
import com.thymeleaf.entity.HistoryEntity;
import org.springframework.stereotype.Component;

@Component
public class HistoryConverter {

    public HistoryEntity toEntity(HistoryDTO dto){
        HistoryEntity entity = new HistoryEntity();
        if (dto.getId() != null){
            entity.setId(dto.getId());
        }
        entity.setPrice(dto.getPrice());
        entity.setQty(dto.getQty());
        entity.setType(dto.getType());
        entity.setActionName(dto.getActionName());
        return entity;
    }

    public HistoryEntity toEntity(HistoryDTO dto, HistoryEntity entity){
        entity.setPrice(dto.getPrice());
        entity.setQty(dto.getQty());
        return entity;
    }

    public HistoryDTO toDTO(HistoryEntity entity){
        HistoryDTO dto = new HistoryDTO();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProduct().getId());
        dto.setQty(entity.getQty());
        dto.setPrice(entity.getPrice());
        dto.setType(entity.getType());
        dto.setActionName(entity.getActionName());
        dto.setActiveFlag(entity.getActiveFlag());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setModifiedDate(entity.getModifiedDate());
        return dto;
    }


}
