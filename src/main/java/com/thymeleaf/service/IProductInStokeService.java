package com.thymeleaf.service;

import com.thymeleaf.dto.InvoiceDTO;
import com.thymeleaf.dto.ProductInStokeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductInStokeService {
    ProductInStokeDTO save(InvoiceDTO dto);
//    void delete(Integer[] ids);
    Page<ProductInStokeDTO> findAll(Pageable pageable);
    List<ProductInStokeDTO> findAll();
    ProductInStokeDTO findById(Integer id);
    ProductInStokeDTO findByProduct_Id(Integer id);
    Page<ProductInStokeDTO> findByProduct_NameContaining(String keyword, Pageable pageable);
    Page<ProductInStokeDTO> findWithDynamicFilters(String keyword, String cateCode, Pageable pageable);
}
