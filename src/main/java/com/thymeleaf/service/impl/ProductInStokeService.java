package com.thymeleaf.service.impl;

import com.thymeleaf.converter.ProductInStokeConverter;
import com.thymeleaf.dto.InvoiceDTO;
import com.thymeleaf.dto.ProductInStokeDTO;
import com.thymeleaf.entity.ProductInStokeEntity;
import com.thymeleaf.entity.ProductInfoEntity;
import com.thymeleaf.repository.IProductInStokeRepository;
import com.thymeleaf.repository.IProductRepository;
import com.thymeleaf.service.IProductInStokeService;
import com.thymeleaf.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductInStokeService implements IProductInStokeService {

    private final ProductInStokeConverter productInStokeConverter;

    private final IProductRepository productRepository;

    private final IProductInStokeRepository productInStokeRepository;

    private final IProductService productService;

    @Override
    @Transactional
    public ProductInStokeDTO save(InvoiceDTO invoiceDTO) {
        ProductInStokeDTO pt = findByProduct_Id(invoiceDTO.getProductId());
        ProductInStokeEntity productInStokeEntity;
        if (pt != null) {
            pt.setQty(pt.getQty() + invoiceDTO.getQty());
            productInStokeEntity = productInStokeRepository.findById(pt.getId()).get();
            productInStokeEntity = productInStokeConverter.toEntity(pt, productInStokeEntity);
        } else {
            pt = new ProductInStokeDTO();
//            pt.setProductId(invoiceDTO.getProductId());
            pt.setQty(invoiceDTO.getQty());
            pt.setPrice(invoiceDTO.getPrice());
            productInStokeEntity = productInStokeConverter.toEntity(pt);
        }
//        ProductInStokeEntity productInStokeEntity = productInStokeConverter.toEntity(pt);

        Optional<ProductInfoEntity> optional = productRepository.findById(invoiceDTO.getProductId());
        if (optional.isPresent()) {
            productInStokeEntity.setProduct(optional.get());
        }
        productInStokeEntity = productInStokeRepository.save(productInStokeEntity);
        return productInStokeConverter.toDTO(productInStokeEntity);
    }

//    @Override
//    public void delete(Integer[] ids) {
//        productRepository.deleteAllByIdInBatch(Arrays.asList(ids));
//    }

    @Override
    public Page<ProductInStokeDTO> findAll(Pageable pageable) {
        Page<ProductInStokeEntity> entityPage = productInStokeRepository.findAll(pageable);
        List<ProductInStokeDTO> dtoList = entityPage.stream().map(productInStoke -> {
            return productInStokeConverter.toDTO(productInStoke);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    public List<ProductInStokeDTO> findAll() {
        List<ProductInStokeEntity> results = productInStokeRepository.findAll();
        return results.stream().map(productInStoke -> {
            return productInStokeConverter.toDTO(productInStoke);
        }).collect(Collectors.toList());
    }

    @Override
    public ProductInStokeDTO findById(Integer id) {
        Optional<ProductInStokeEntity> optional = productInStokeRepository.findById(id);
        if (optional.isPresent()) {
            return productInStokeConverter.toDTO(optional.get());
        }
        return null;
    }

    @Override
    public ProductInStokeDTO findByProduct_Id(Integer id) {
        Optional<ProductInStokeEntity> optional = productInStokeRepository.findByProduct_Id(id);
        if (optional.isPresent()) {
            return productInStokeConverter.toDTO(optional.get());
        }
        return null;
    }

    @Override
    public Page<ProductInStokeDTO> findByProduct_NameContaining(String keyword, Pageable pageable) {
        keyword = "%" + keyword + "%";
        Page<ProductInStokeEntity> entityPage = productInStokeRepository.findByProduct_NameContaining(keyword, pageable);
        List<ProductInStokeDTO> dtoList = entityPage.stream().map(productInStoke -> {
            return productInStokeConverter.toDTO(productInStoke);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    public Page<ProductInStokeDTO> findWithDynamicFilters(String keyword, String cateCode, Pageable pageable) {
        keyword = "%" + keyword + "%";
        Page<ProductInStokeEntity> entityPage = productInStokeRepository.findWithDynamicFilters(keyword, cateCode, pageable);
        List<ProductInStokeDTO> dtoList = entityPage.stream().map(productInStoke -> {
            return productInStokeConverter.toDTO(productInStoke);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }
}
