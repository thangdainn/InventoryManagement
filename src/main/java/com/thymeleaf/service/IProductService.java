package com.thymeleaf.service;

import com.thymeleaf.dto.ProductInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface IProductService {
    ProductInfoDTO save(ProductInfoDTO dto) throws IOException;
    void delete(Integer[] ids);
    Page<ProductInfoDTO> findAll(Integer activeFlag, Pageable pageable);
    List<ProductInfoDTO> findAll();
    List<ProductInfoDTO> findByProductInStoke();
    ProductInfoDTO findByCode(String code);
    ProductInfoDTO findById(Integer id);
    Page<ProductInfoDTO> findByNameContaining(String keyword, Integer activeFlag, Pageable pageable);
    Page<ProductInfoDTO> findWithDynamicFilters(String keyword, String categoryCode, Integer activeFlag, Pageable pageable);
}
