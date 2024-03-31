package com.thymeleaf.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class RefreshTokenDTO {

    @NotBlank(message = "RefreshToken is required")
    private String refreshToken;
}
