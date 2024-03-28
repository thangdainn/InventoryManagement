package com.thymeleaf.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoDTO extends AbstractDTO<ProductInfoDTO>{

    @NotNull(message = "Category is required")
    private Integer categoryId;

    private CategoryDTO category;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Code is required")
    private String code;

    private String description;
    private String imgUrl;

    private MultipartFile multipartFile;
}
