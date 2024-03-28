package com.thymeleaf.service.impl;

import com.thymeleaf.converter.ProductInfoConverter;
import com.thymeleaf.dto.ProductInfoDTO;
import com.thymeleaf.entity.CategoryEntity;
import com.thymeleaf.entity.ProductInfoEntity;
import com.thymeleaf.repository.ICategoryRepository;
import com.thymeleaf.repository.IProductRepository;
import com.thymeleaf.service.IProductService;
import com.thymeleaf.utils.ConfigLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductInfoConverter productInfoConverter;

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private ICategoryRepository categoryRepository;

//    @Value("classpath:static/upload")
//    private Resource resourcePath;

    @Override
    public ProductInfoDTO save(ProductInfoDTO dto) throws IOException {
        ProductInfoEntity productInfoEntity = new ProductInfoEntity();
        String folder = "/upload/";
        if (dto.getId() != null) {
            Optional<ProductInfoEntity> optional = productRepository.findById(dto.getId());
            if (optional.isPresent()) {
                ProductInfoEntity oldProductEntity = optional.get();
                if (dto.getMultipartFile() != null){

                    String imgUrl = processUpLoadFile(dto.getMultipartFile());
                    dto.setImgUrl(folder + imgUrl);
                }
                productInfoEntity = productInfoConverter.toEntity(dto, oldProductEntity);
            }
        } else {
            String imgUrl = processUpLoadFile(dto.getMultipartFile());
            if (imgUrl == null){
                return null;
            }
            dto.setImgUrl(folder + imgUrl);
            productInfoEntity = productInfoConverter.toEntity(dto);
        }
        Optional<CategoryEntity> categoryEntity = categoryRepository.findById(dto.getCategoryId());
        if (categoryEntity.isPresent()){
            productInfoEntity.setCategory(categoryEntity.get());
        }
        productInfoEntity = productRepository.save(productInfoEntity);
        return productInfoConverter.toDTO(productInfoEntity);
    }

    @Override
    public void delete(Integer[] ids) {
        productRepository.deleteAllByIdInBatchCustom(Arrays.asList(ids));
    }

    @Override
    public Page<ProductInfoDTO> findAll(Integer activeFlag, Pageable pageable) {
        Page<ProductInfoEntity> entityPage = productRepository.findAllByActiveFlag(activeFlag, pageable);
        List<ProductInfoDTO> dtoList = entityPage.stream().map(product -> {
            return productInfoConverter.toDTO(product);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    public List<ProductInfoDTO> findAll() {
        List<ProductInfoEntity> results = productRepository.findAll();
        return results.stream().map(product -> {
            return productInfoConverter.toDTO(product);
        }).collect(Collectors.toList());
    }

    @Override
    public List<ProductInfoDTO> findByProductInStoke() {
        List<ProductInfoEntity> results = productRepository.findByProductInStoke();
        return results.stream().map(product -> {
            return productInfoConverter.toDTO(product);
        }).collect(Collectors.toList());
    }

    @Override
    public ProductInfoDTO findByCode(String code) {
        Optional<ProductInfoEntity> optional = productRepository.findByCode(code);
        if (optional.isPresent()) {
            return productInfoConverter.toDTO(optional.get());
        }
        return null;
    }

    @Override
    public ProductInfoDTO findById(Integer id) {
        Optional<ProductInfoEntity> optional = productRepository.findById(id);
        if (optional.isPresent()) {
            return productInfoConverter.toDTO(optional.get());
        }
        return null;
    }

    @Override
    public Page<ProductInfoDTO> findByNameContaining(String keyword, Integer activeFlag, Pageable pageable) {
        Page<ProductInfoEntity> entityPage = productRepository.findByNameContainingAndActiveFlag(keyword, activeFlag, pageable);
        List<ProductInfoDTO> dtoList = entityPage.stream().map(product -> {
            return productInfoConverter.toDTO(product);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    public Page<ProductInfoDTO> findWithDynamicFilters(String keyword, String categoryCode, Integer activeFlag, Pageable pageable) {
        keyword = "%" + keyword + "%";
        Page<ProductInfoEntity> entityPage = productRepository.findWithDynamicFilters(keyword, categoryCode, activeFlag, pageable);
        List<ProductInfoDTO> dtoList = entityPage.stream().map(product -> {
            return productInfoConverter.toDTO(product);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    private String processUpLoadFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile != null){
            String fileName = multipartFile.getOriginalFilename();
            String randomId = UUID.randomUUID().toString();
            fileName = randomId.concat(fileName.substring(fileName.lastIndexOf(".")));
            String uploadLocation = ConfigLoader.getInstance().getValue("upload.spring.location");
            if (!Files.exists(Path.of(uploadLocation))){
                Files.createDirectories(Path.of(uploadLocation));
                return null;
            }
            Path path = Paths.get(uploadLocation + fileName);
            Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);


            File saveFile = new ClassPathResource(ConfigLoader.getInstance().getValue("upload.location")).getFile();
            path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
            Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        }
        return null;
    }
}
