package com.thymeleaf.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDTO extends AbstractDTO<UserDTO>{

    @NotBlank(message = "Username is required")
    private String userName;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Role is required")
    @NotEmpty(message = "Role is required")
    private Integer[] roleIds;

    private List<RoleDTO> roles = new ArrayList<>();
}
