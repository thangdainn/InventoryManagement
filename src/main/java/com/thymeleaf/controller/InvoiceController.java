package com.thymeleaf.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thymeleaf.api.request.InvoiceInput;
import com.thymeleaf.api.response.InvoiceOutput;
import com.thymeleaf.dto.CategoryDTO;
import com.thymeleaf.dto.InvoiceDTO;
import com.thymeleaf.dto.MenuDTO;
import com.thymeleaf.dto.ProductInfoDTO;
import com.thymeleaf.service.ICategoryService;
import com.thymeleaf.service.IInvoiceService;
import com.thymeleaf.service.IProductService;
import com.thymeleaf.service.InvoiceReport;
import com.thymeleaf.utils.Constant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping(value = {"/goods-receipt", "/goods-issue"})
public class InvoiceController {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IInvoiceService invoiceService;

    @GetMapping(value = {"/list"})
    public String showInvoice(@ModelAttribute("invoices") InvoiceInput input,
                               @PageableDefault(size = 5) Pageable pageable,
                               Model model, HttpSession session,
                               HttpServletRequest request) throws JsonProcessingException {
        InvoiceOutput result = new InvoiceOutput();
        Integer type = checkType(request, result);
        input.setKeyword(input.getKeyword().trim());
        result.setType(type);
        result.setKeyword(input.getKeyword());
        result.setPage(pageable.getPageNumber() + 1);
        result.setSize(pageable.getPageSize());
        result.setCategoryCode(input.getCategoryCode());
        result.setFromDate(getToDateAsTimestamp(input.getFromDate()));
        result.setToDate(getToDateAsTimestamp(input.getToDate()));
        Page<InvoiceDTO> page;
        if (!input.getCategoryCode().equals("") || result.getFromDate() != null || result.getToDate() != null) {
            if (input.getCategoryCode().equals("")){
                input.setCategoryCode(null);
            }
            page = invoiceService.findWithDynamicFilters(input.getKeyword(), input.getCategoryCode(), type,
                    result.getFromDate(), result.getToDate(), pageable);
        } else if (!input.getKeyword().equals("")) {
            page = invoiceService.findByTypeAndProduct_NameContaining(input.getKeyword(), type, pageable);
        } else {
            page = invoiceService.findByType(type, pageable);
        }
        List<InvoiceDTO> invoiceDTOS = page.getContent();
        for (InvoiceDTO dto : invoiceDTOS) {
            dto.setProductInfo(productService.findById(dto.getProductId()));
        }
        result.setListResult(invoiceDTOS);
        result.setTotalPage(page.getTotalPages());
        common(model, session, null, result);
        return "/fragments/contents/invoice/list";
    }

    @GetMapping(value = {"/edit/{code}"})
    public String showInvoiceEdit(@PathVariable(name = "code") String code, Model model, HttpSession session,
                                   HttpServletRequest request) {
        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setCode(code);
        invoice = invoiceService.findByCode(invoice.getCode());
        Integer type = checkType(request, null);
        invoice.setType(type);
        common(model, session, invoice, null);
        List<ProductInfoDTO> products;
        if (type.equals(1)){
            products = productService.findAll();
        } else {
            products = productService.findByProductInStoke();
        }
        model.addAttribute(Constant.PRODUCTS, products);
        return "/fragments/contents/invoice/edit";
    }

    @GetMapping(value = {"/add"})
    public String showInvoiceEdit(Model model, HttpSession session, HttpServletRequest request) {
        InvoiceDTO invoice = new InvoiceDTO();
        Integer type = checkType(request, null);
        invoice.setType(type);
        common(model, session, invoice, null);
        List<ProductInfoDTO> products;
        if (type.equals(1)){
            products = productService.findAll();
        } else {
            products = productService.findByProductInStoke();
        }
        model.addAttribute(Constant.PRODUCTS, products);
        return "/fragments/contents/invoice/edit";
    }

    @GetMapping(value = {"/export"})
    public ModelAndView exportReport(HttpServletRequest request) throws JsonProcessingException {
        Integer type = checkType(request, null);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("type", type);
        List<InvoiceDTO> invoices = invoiceService.findByType(type);
        for (InvoiceDTO dto : invoices) {
            dto.setProductInfo(productService.findById(dto.getProductId()));
        }
        if (type.equals(Constant.TYPE_GOODS_RECEIPT)){
            modelAndView.addObject(Constant.KEY_GOODS_RECEIPT_REPORT, invoices);
        } else {
            modelAndView.addObject(Constant.KEY_GOODS_ISSUE_REPORT, invoices);
        }
        modelAndView.setView(new InvoiceReport());
        return modelAndView;
    }

    private void common(Model model, HttpSession session, InvoiceDTO invoice, InvoiceOutput result) {
        List<MenuDTO> menuList = (List<MenuDTO>) session.getAttribute(Constant.MENUS);
        UserDetails user = (UserDetails) session.getAttribute(Constant.USER);
        if (result != null) {
            List<CategoryDTO> categories = categoryService.findAll();
            model.addAttribute(Constant.CATEGORIES, categories);
            model.addAttribute(Constant.INVOICES, result);
        }
        if (invoice != null) {
            model.addAttribute(Constant.INVOICE, invoice);
        }
        model.addAttribute(Constant.MENUS, menuList);
        model.addAttribute(Constant.USER, user);
    }

    private Integer checkType(HttpServletRequest request, InvoiceOutput result) {
        String requestURI = request.getRequestURI();
        if (requestURI.contains("goods-receipt")) {
            if (result != null) {
                result.setUrl("/goods-receipt");
            }
            return 1;
        }
        if (result != null) {
            result.setUrl("/goods-issue");
        }
        return 2;
    }

    public Timestamp getToDateAsTimestamp(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date parsedDate = dateFormat.parse(date);
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
