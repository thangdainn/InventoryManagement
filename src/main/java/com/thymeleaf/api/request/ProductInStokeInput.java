package com.thymeleaf.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInStokeInput {
    private String keyword = "";
    private String categoryCode = "";
    private Integer activeFlag = 1;
}
