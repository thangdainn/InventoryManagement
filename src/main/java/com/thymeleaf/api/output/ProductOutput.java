package com.thymeleaf.api.output;

import com.thymeleaf.dto.ProductInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOutput {
    private Integer page;
    private Integer size;
    private Integer totalPage;
    private String keyword;
    private String categoryCode;
    private List<ProductInfoDTO> listResult = new ArrayList<>();
}
