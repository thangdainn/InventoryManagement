package com.thymeleaf.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO extends AbstractDTO<TokenDTO>{

    @NotBlank(message = "Token is required")
    private String token;

    private Timestamp expirationDate;

    @NotBlank(message = "RefreshToken is required")
    private String refreshToken;

    private Timestamp refreshTokenExpirationDate;

    private Integer userId;

}
