package com.thymeleaf.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDTO extends AbstractDTO<InvoiceDTO>{

    @NotNull(message = "Product is required")
    private Integer productId;

    private ProductInfoDTO productInfo;

    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "Quantity is required")
    private Integer qty;

    @NotNull(message = "Price is required")
    private Integer price;

    private Integer type;
}
