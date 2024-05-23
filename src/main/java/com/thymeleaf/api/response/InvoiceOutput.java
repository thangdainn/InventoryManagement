package com.thymeleaf.api.response;

import com.thymeleaf.dto.InvoiceDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceOutput {
    private Integer page;
    private Integer size;
    private Integer totalPage;
    private String keyword;
    private String categoryCode;
    private Integer type;
    private String url;
    private Timestamp fromDate;
    private Timestamp toDate;
    private List<InvoiceDTO> listResult = new ArrayList<>();
}
