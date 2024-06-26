package com.thymeleaf.api.request;

import com.thymeleaf.utils.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryInput {
    private String keyword = "";
    private String categoryCode = "";
    private Integer type = Constant.TYPE_ALL;
    private Integer activeFlag = 1;
}
