package com.thymeleaf.service;

import com.thymeleaf.dto.InvoiceDTO;
import com.thymeleaf.utils.Constant;
import com.thymeleaf.utils.DateUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import java.util.List;
import java.util.Map;

public class InvoiceReport extends AbstractXlsView {
    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment;filename=\"invoice-export.xlsx.xls\"");
        Sheet sheet = workbook.createSheet("data");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("No");
        header.createCell(1).setCellValue("Code");
        header.createCell(2).setCellValue("Product");
        header.createCell(3).setCellValue("Quantity");
        header.createCell(4).setCellValue("Price");
        header.createCell(5).setCellValue("Created Date");
        header.createCell(6).setCellValue("Modified Date");
        Integer type = (Integer) model.get("type");
        List<InvoiceDTO> invoices;
        if (type.equals(Constant.TYPE_GOODS_RECEIPT)){
            invoices = (List<InvoiceDTO>) model.get(Constant.KEY_GOODS_RECEIPT_REPORT);
        } else {
            invoices = (List<InvoiceDTO>) model.get(Constant.KEY_GOODS_ISSUE_REPORT);
        }
        int rownum = 1;
        for (InvoiceDTO invoice : invoices){
            Row row = sheet.createRow(rownum++);
            row.createCell(0).setCellValue(rownum - 1);
            row.createCell(1).setCellValue(invoice.getCode());
            row.createCell(2).setCellValue(invoice.getProductInfo().getName());
            row.createCell(3).setCellValue(invoice.getQty());
            row.createCell(4).setCellValue(invoice.getPrice());
            row.createCell(5).setCellValue(DateUtil.dateToString(invoice.getCreatedDate()));
            row.createCell(6).setCellValue(DateUtil.dateToString(invoice.getModifiedDate()));
        }
    }
}
