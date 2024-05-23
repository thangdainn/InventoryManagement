package com.thymeleaf.api.response;

import com.thymeleaf.dto.CategoryDTO;
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
public class CategoryOutput {
    private Integer page;
    private Integer size;
    private Integer totalPage;
    private String keyword;
    private List<CategoryDTO> listResult = new ArrayList<>();
}
