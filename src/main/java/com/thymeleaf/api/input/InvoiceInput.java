package com.thymeleaf.api.input;

import jakarta.validation.constraints.NotNull;
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
//    @NotNull
    private String typeInvoice;
    private Integer type;
    @DateTimeFormat(pattern = "YYYY-MM-DD HH:mm:ss")
    private String fromDate;
    @DateTimeFormat(pattern = "YYYY-MM-DD HH:mm:ss")
    private String toDate;
    private Integer activeFlag = 1;
    private Integer page;
    private Integer limit = 5;
    private String sort;
}
