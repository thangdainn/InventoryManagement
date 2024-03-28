package com.thymeleaf.service.impl;

import com.thymeleaf.converter.HistoryConverter;
import com.thymeleaf.converter.ProductInStokeConverter;
import com.thymeleaf.dto.HistoryDTO;
import com.thymeleaf.dto.InvoiceDTO;
import com.thymeleaf.entity.HistoryEntity;
import com.thymeleaf.entity.ProductInfoEntity;
import com.thymeleaf.repository.IHistoryRepository;
import com.thymeleaf.repository.IProductInStokeRepository;
import com.thymeleaf.repository.IProductRepository;
import com.thymeleaf.service.IHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistoryService implements IHistoryService {

    @Autowired
    private ProductInStokeConverter productInStokeConverter;

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IProductInStokeRepository productInStokeRepository;

    @Autowired
    private HistoryConverter historyConverter;

    @Autowired
    private IHistoryRepository historyRepository;

    @Override
    public HistoryDTO save(InvoiceDTO invoiceDTO, String action){
        HistoryDTO dto = new HistoryDTO();
        dto.setPrice(invoiceDTO.getPrice());
        dto.setQty(invoiceDTO.getQty());
        dto.setProductId(invoiceDTO.getProductId());
        dto.setType(invoiceDTO.getType());
        dto.setActionName(action);
        HistoryEntity historyEntity = historyConverter.toEntity(dto);
        Optional<ProductInfoEntity> optionalProductInfo = productRepository.findById(dto.getProductId());
        if (optionalProductInfo.isPresent()){
            historyEntity.setProduct(optionalProductInfo.get());
        }
        historyEntity = historyRepository.save(historyEntity);
        return historyConverter.toDTO(historyEntity);
    }

//    @Override
//    public void delete(Integer[] ids) {
//        productRepository.deleteAllByIdInBatch(Arrays.asList(ids));
//    }

    @Override
    public Page<HistoryDTO> findAll(Pageable pageable) {
        Page<HistoryEntity> entityPage = historyRepository.findAll(pageable);
        List<HistoryDTO> dtoList = entityPage.stream().map(history -> {
            return historyConverter.toDTO(history);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    public List<HistoryDTO> findAll() {
        List<HistoryEntity> results = historyRepository.findAll();
        return results.stream().map(history -> {
            return historyConverter.toDTO(history);
        }).collect(Collectors.toList());
    }

    @Override
    public HistoryDTO findById(Integer id) {
        Optional<HistoryEntity> optional = historyRepository.findById(id);
        if (optional.isPresent()) {
            return historyConverter.toDTO(optional.get());
        }
        return null;
    }

    @Override
    public Page<HistoryDTO> findByProduct_NameContaining(String keyword, Pageable pageable) {
        keyword = "%" + keyword + "%";
        Page<HistoryEntity> entityPage = historyRepository.findByProduct_NameContaining(keyword, pageable);
        List<HistoryDTO> dtoList = entityPage.stream().map(history -> {
            return historyConverter.toDTO(history);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    public Page<HistoryDTO> findWithDynamicFilters(String keyword, String cateCode, Integer type, Pageable pageable) {
        keyword = "%" + keyword + "%";
        Page<HistoryEntity> entityPage = historyRepository.findWithDynamicFilters(keyword , cateCode, type, pageable);
        List<HistoryDTO> dtoList = entityPage.stream().map(history -> {
            return historyConverter.toDTO(history);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }
}
