package com.thymeleaf.service;

import com.thymeleaf.dto.HistoryDTO;
import com.thymeleaf.dto.InvoiceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IHistoryService {
    HistoryDTO save(InvoiceDTO dto, String action);
//    void delete(Integer[] ids);
    Page<HistoryDTO> findAll(Pageable pageable);
    List<HistoryDTO> findAll();
    HistoryDTO findById(Integer id);
    Page<HistoryDTO> findByProduct_NameContaining(String keyword, Pageable pageable);
    Page<HistoryDTO> findWithDynamicFilters(String keyword, String cateCode, Integer type, Pageable pageable);
}
