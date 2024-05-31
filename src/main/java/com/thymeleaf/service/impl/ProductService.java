package com.thymeleaf.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thymeleaf.converter.ProductInfoConverter;
import com.thymeleaf.dto.ProductInfoDTO;
import com.thymeleaf.entity.CategoryEntity;
import com.thymeleaf.entity.ProductInfoEntity;
import com.thymeleaf.repository.ICategoryRepository;
import com.thymeleaf.repository.IProductRepository;
import com.thymeleaf.service.IProductService;
import com.thymeleaf.utils.PageWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductInfoConverter productInfoConverter;

    private final IProductRepository productRepository;

    private final ICategoryRepository categoryRepository;

    private final BaseRedisService baseRedisService;

    private final ObjectMapper objectMapper;
    private static final String CACHE_PREFIX = "Product::";

    private final Cloudinary cloudinary;

//    @Value("classpath:static/upload")
//    private Resource resourcePath;

    @Override
    public ProductInfoDTO save(ProductInfoDTO dto) throws IOException {
        ProductInfoEntity productInfoEntity = new ProductInfoEntity();
//        String folder = "/upload/";
        boolean isExist = false;
        if (dto.getId() != null) {
            isExist = true;
            Optional<ProductInfoEntity> optional = productRepository.findById(dto.getId());
            if (optional.isPresent()) {
                ProductInfoEntity oldProductEntity = optional.get();
                if (dto.getMultipartFile() != null) {
                    try {
                        Map r = cloudinary.uploader().upload(dto.getMultipartFile().getBytes(),
                                ObjectUtils.asMap("resource_type", "auto"));
                        String imgUrl = r.get("secure_url").toString();
                        dto.setImgUrl(imgUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                productInfoEntity = productInfoConverter.toEntity(dto, oldProductEntity);
            }
        } else {
            try {
                Map r = cloudinary.uploader().upload(dto.getMultipartFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                String imgUrl = r.get("secure_url").toString();
                dto.setImgUrl(imgUrl);
                productInfoEntity = productInfoConverter.toEntity(dto);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        Optional<CategoryEntity> categoryEntity = categoryRepository.findById(dto.getCategoryId());
        if (categoryEntity.isPresent()) {
            productInfoEntity.setCategory(categoryEntity.get());
        }
        productInfoEntity = productRepository.save(productInfoEntity);
        if (isExist) {
//            baseRedisService.delete(CACHE_PREFIX + productInfoEntity.getId());
            baseRedisService.flushDb();
        }
        dto = productInfoConverter.toDTO(productInfoEntity);
        baseRedisService.set(CACHE_PREFIX + dto.getId(), objectMapper.writeValueAsString(dto));
        baseRedisService.setTimeToValue(CACHE_PREFIX + dto.getId(), 1);
        return productInfoConverter.toDTO(productInfoEntity);
    }

    @Override
    public void delete(Integer[] ids) {
        productRepository.deleteAllByIdInBatchCustom(Arrays.asList(ids));
        baseRedisService.flushDb();

//        for (Integer id : ids) {
//            baseRedisService.delete(CACHE_PREFIX + id);
//        }
    }

    @Override
    public Page<ProductInfoDTO> findAll(Integer activeFlag, Pageable pageable) {
        String cacheKey = CACHE_PREFIX + "activeFlag:" + activeFlag + "::page:" + pageable.getPageNumber() + "::size:" + pageable.getPageSize() + "::sort:" + pageable.getSort();
        Object cache = baseRedisService.get(cacheKey);
        if (cache != null) {
            try {
                PageWrapper<ProductInfoDTO> cachePage = objectMapper.readValue(cache.toString(), new TypeReference<PageWrapper<ProductInfoDTO>>() {
                });
                Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
                return new PageImpl<>(cachePage.getList(), pageable, cachePage.getTotalElements());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        Page<ProductInfoEntity> entityPage = productRepository.findAllByActiveFlag(activeFlag, pageable);
        List<ProductInfoDTO> dtoList = entityPage.stream().map(productInfoConverter::toDTO).toList();
        PageWrapper<ProductInfoDTO> pageWrapper = new PageWrapper<>(dtoList, entityPage.getTotalPages(), entityPage.getTotalElements());
        try {
            baseRedisService.set(cacheKey, objectMapper.writeValueAsString(pageWrapper));
            baseRedisService.setTimeToValue(cacheKey, 1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    public List<ProductInfoDTO> findAll() {

        Object cache = baseRedisService.get(CACHE_PREFIX + "all");
        if (cache != null) {
            try {

                return objectMapper.readValue(cache.toString(), new TypeReference<List<ProductInfoDTO>>() {
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        List<ProductInfoEntity> results = productRepository.findAll();
        List<ProductInfoDTO> dtoList = results.stream().map(productInfoConverter::toDTO).toList();
        try {
            baseRedisService.set(CACHE_PREFIX + "all", objectMapper.writeValueAsString(dtoList));
            baseRedisService.setTimeToValue(CACHE_PREFIX + "all", 1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return dtoList;
    }

    @Override
    public List<ProductInfoDTO> findAllByActiveFlag(Integer activeFlag) {
        List<ProductInfoEntity> results = productRepository.findAllByActiveFlag(activeFlag);
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
    public ProductInfoDTO findByCode(String code) throws JsonProcessingException {
        Object cache = baseRedisService.get(CACHE_PREFIX + code);
        if (cache != null) {
//            baseRedisService.delete(CACHE_PREFIX + code);
            return objectMapper.readValue(cache.toString(), ProductInfoDTO.class);
        }
        Optional<ProductInfoEntity> optional = productRepository.findByCode(code);
        if (optional.isPresent()) {
            ProductInfoDTO dto = productInfoConverter.toDTO(optional.get());
            baseRedisService.set(CACHE_PREFIX + code, objectMapper.writeValueAsString(dto));
            baseRedisService.setTimeToValue(CACHE_PREFIX + code, 1);
            return productInfoConverter.toDTO(optional.get());
        }
        return null;
    }

    @Override
    public ProductInfoDTO findById(Integer id) throws JsonProcessingException {
        Object cache = baseRedisService.get(CACHE_PREFIX + id);
        if (cache != null) {
            return objectMapper.readValue(cache.toString(), ProductInfoDTO.class);
        }
        Optional<ProductInfoEntity> optional = productRepository.findById(id);
        if (optional.isPresent()) {
            ProductInfoDTO dto = productInfoConverter.toDTO(optional.get());
            baseRedisService.set(CACHE_PREFIX + id, objectMapper.writeValueAsString(dto));
            baseRedisService.setTimeToValue(CACHE_PREFIX + id, 1);
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
    public Page<ProductInfoDTO> findWithDynamicFilters(String keyword, String categoryCode, Integer
            activeFlag, Pageable pageable) {
        keyword = "%" + keyword + "%";
        Page<ProductInfoEntity> entityPage = productRepository.findWithDynamicFilters(keyword, categoryCode, activeFlag, pageable);
        List<ProductInfoDTO> dtoList = entityPage.stream().map(product -> {
            return productInfoConverter.toDTO(product);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

//    private String processUpLoadFile(MultipartFile multipartFile) throws IOException {
//        if (multipartFile != null) {
//            String fileName = multipartFile.getOriginalFilename();
//            String randomId = UUID.randomUUID().toString();
//            fileName = randomId.concat(fileName.substring(fileName.lastIndexOf(".")));
//            String uploadLocation = ConfigLoader.getInstance().getValue("upload.spring.location");
//            if (!Files.exists(Path.of(uploadLocation))) {
//                Files.createDirectories(Path.of(uploadLocation));
//                return null;
//            }
//            Path path = Paths.get(uploadLocation + fileName);
//            Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//
//
//            File saveFile = new ClassPathResource(ConfigLoader.getInstance().getValue("upload.location")).getFile();
//            path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
//            Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//
//            return fileName;
//        }
//        return null;
//    }
}
