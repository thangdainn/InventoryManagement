package com.thymeleaf.api.input;

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
}
