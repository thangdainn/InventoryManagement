package com.thymeleaf.service.impl;

import com.thymeleaf.converter.InvoiceConverter;
import com.thymeleaf.dto.InvoiceDTO;
import com.thymeleaf.dto.ProductInStokeDTO;
import com.thymeleaf.entity.InvoiceEntity;
import com.thymeleaf.entity.ProductInfoEntity;
import com.thymeleaf.repository.IInvoiceRepository;
import com.thymeleaf.repository.IProductRepository;
import com.thymeleaf.service.IHistoryService;
import com.thymeleaf.service.IInvoiceService;
import com.thymeleaf.service.IProductInStokeService;
import com.thymeleaf.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService implements IInvoiceService {

    @Autowired
    private InvoiceConverter invoiceConverter;

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IInvoiceRepository invoiceRepository;

    @Autowired
    private IProductInStokeService productInStokeService;

    @Autowired
    private IHistoryService historyService;

    @Override
    public InvoiceDTO save(InvoiceDTO dto){
        InvoiceEntity invoiceEntity = new InvoiceEntity();
        Optional<ProductInfoEntity> optional = productRepository.findById(dto.getProductId());
//        if (optional.isPresent()){
//            invoiceEntity.setProduct(optional.get());
//        }
        if (dto.getId() != null) {
            Optional<InvoiceEntity> optionalInvoice = invoiceRepository.findById(dto.getId());
            if (optionalInvoice.isPresent()) {
                InvoiceEntity oldInvoice = optionalInvoice.get();
                Integer oldQty = oldInvoice.getQty();
                invoiceEntity = invoiceConverter.toEntity(dto, oldInvoice);
                if (optional.isPresent()){
                    invoiceEntity.setProduct(optional.get());
                }
                historyService.save(invoiceConverter.toDTO(invoiceEntity), Constant.ACTION_EDIT);
                invoiceEntity.setQty(dto.getQty() - oldQty);
                if (dto.getType().equals(Constant.TYPE_GOODS_ISSUE)){
                    invoiceEntity.setQty(-invoiceEntity.getQty());
                }
            }
        } else {
            invoiceEntity = invoiceConverter.toEntity(dto);
            if (optional.isPresent()){
                invoiceEntity.setProduct(optional.get());
            }
            if (dto.getType().equals(Constant.TYPE_GOODS_ISSUE)){
                invoiceEntity.setQty(- dto.getQty());
            }
            historyService.save(invoiceConverter.toDTO(invoiceEntity), Constant.ACTION_ADD);
        }
        productInStokeService.save(invoiceConverter.toDTO(invoiceEntity));
        invoiceEntity.setQty(dto.getQty());
        invoiceEntity = invoiceRepository.save(invoiceEntity);
        return invoiceConverter.toDTO(invoiceEntity);
    }

//    @Override
//    public void delete(Integer[] ids) {
//        for (Integer id : ids){
//            InvoiceDTO invoiceDTO = findById(id);
//            ProductInStokeDTO productInStokeDTO = productInStokeService.findByProduct_Id(invoiceDTO.getProductId());
//            if (invoiceDTO.getType().equals(Constant.TYPE_GOODS_RECEIPT)){
//                productInStokeService.save()
//            } else {
//
//            }
//        }
//        invoiceRepository.deleteAllByIdInBatch(Arrays.asList(ids));
//    }

//    @Override
//    public Page<InvoiceDTO> findAll(Pageable pageable) {
//        Page<InvoiceEntity> entityPage = invoiceRepository.findAll(pageable);
//        List<InvoiceDTO> dtoList = entityPage.stream().map(invoice -> {
//            return invoiceConverter.toDTO(invoice);
//        }).collect(Collectors.toList());
//        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
//    }

    @Override
    public List<InvoiceDTO> findByType(Integer type) {
        List<InvoiceEntity> results = invoiceRepository.findByType(type);
        return results.stream().map(invoice -> {
            return invoiceConverter.toDTO(invoice);
        }).collect(Collectors.toList());
    }

    @Override
    public Page<InvoiceDTO> findByType(Integer type, Pageable pageable) {
        Page<InvoiceEntity> entityPage = invoiceRepository.findByType(type, pageable);
        List<InvoiceDTO> dtoList = entityPage.stream().map(invoice -> {
            return invoiceConverter.toDTO(invoice);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    public InvoiceDTO findById(Integer id) {
        Optional<InvoiceEntity> optional = invoiceRepository.findById(id);
        if (optional.isPresent()) {
            return invoiceConverter.toDTO(optional.get());
        }
        return null;
    }

    @Override
    public InvoiceDTO findByCode(String code) {
        Optional<InvoiceEntity> optional = invoiceRepository.findByCode(code);
        if (optional.isPresent()) {
            return invoiceConverter.toDTO(optional.get());
        }
        return null;
    }

    @Override
    public Page<InvoiceDTO> findByTypeAndProduct_NameContaining(String keyword, Integer type, Pageable pageable) {
        keyword = "%" + keyword + "%";
        Page<InvoiceEntity> entityPage = invoiceRepository.findByTypeAndProduct_NameContaining(keyword, type, pageable);
        List<InvoiceDTO> dtoList = entityPage.stream().map(invoice -> {
            return invoiceConverter.toDTO(invoice);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    public Page<InvoiceDTO> findWithDynamicFilters(String keyword, String cateCode, Integer type,
                                                   Timestamp fromDate, Timestamp toDate, Pageable pageable) {
        keyword = "%" + keyword + "%";
        Page<InvoiceEntity> entityPage = invoiceRepository.findWithDynamicFilters(keyword , cateCode, type, fromDate, toDate, pageable);
        List<InvoiceDTO> dtoList = entityPage.stream().map(history -> {
            return invoiceConverter.toDTO(history);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }
}
