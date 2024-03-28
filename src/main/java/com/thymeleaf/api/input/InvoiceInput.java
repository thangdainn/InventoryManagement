package com.thymeleaf.api.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceInput {
    private String keyword = "";
    private String categoryCode = "";
    private Integer type;
    @DateTimeFormat(pattern = "YYYY-MM-DD HH:mm:ss")
    private String fromDate;
    @DateTimeFormat(pattern = "YYYY-MM-DD HH:mm:ss")
    private String toDate;
}
