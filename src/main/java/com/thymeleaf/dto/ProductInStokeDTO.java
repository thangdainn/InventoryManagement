package com.thymeleaf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductInStokeDTO extends AbstractDTO<ProductInStokeDTO>{
    private Integer productId;
    private ProductInfoDTO productInfo;
    private Integer qty;
    private Integer price;
    private String categoryId;
}
