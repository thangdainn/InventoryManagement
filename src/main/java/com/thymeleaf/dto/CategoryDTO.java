package com.thymeleaf.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO extends AbstractDTO<CategoryDTO>{

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Code is required")
    private String code;

    private String description;
}
