package com.thymeleaf.api.output;

import com.thymeleaf.dto.MenuDTO;
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
public class MenuOutput {
    private Integer page;
    private Integer size;
    private Integer totalPage;
    private List<MenuDTO> listResult = new ArrayList<>();
}
