package com.thymeleaf.service;

import com.thymeleaf.dto.InvoiceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;

public interface IInvoiceService {
    InvoiceDTO save(InvoiceDTO dto);
//    void delete(Integer[] ids);
//    Page<InvoiceDTO> findAll(Pageable pageable);
    List<InvoiceDTO> findByType(Integer type);
    Page<InvoiceDTO> findByType(Integer type, Pageable pageable);
    InvoiceDTO findById(Integer id);
    InvoiceDTO findByCode(String code);
    Page<InvoiceDTO> findByTypeAndProduct_NameContaining(String keyword, Integer type, Pageable pageable);
    Page<InvoiceDTO> findWithDynamicFilters(String keyword, String cateCode, Integer type,
                                            Timestamp fromDate, Timestamp toDate, Pageable pageable);
}
