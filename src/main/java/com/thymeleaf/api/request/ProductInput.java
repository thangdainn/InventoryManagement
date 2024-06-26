package com.thymeleaf.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInput {
    private String keyword = "";
    private String categoryCode = "";
    private Integer activeFlag = 1;
    private Integer page;
    private Integer limit = 4;
    private String sort;
}
