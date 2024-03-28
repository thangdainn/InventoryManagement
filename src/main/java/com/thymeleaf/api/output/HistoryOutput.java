package com.thymeleaf.api.output;

import com.thymeleaf.dto.HistoryDTO;
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
public class HistoryOutput {
    private Integer page;
    private Integer size;
    private Integer totalPage;
    private String keyword;
    private String categoryCode;
    private Integer type;
    private List<HistoryDTO> listResult = new ArrayList<>();
}
