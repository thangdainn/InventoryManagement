package com.thymeleaf.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleDTO extends AbstractDTO<RoleDTO>{

    @NotBlank(message = "Name is required")
    private String name;
    private String description;
//    private List<AuthDTO> auths = new ArrayList<>();
}