package com.thymeleaf.controller;

import com.thymeleaf.api.input.HistoryInput;
import com.thymeleaf.api.output.HistoryOutput;
import com.thymeleaf.dto.CategoryDTO;
import com.thymeleaf.dto.HistoryDTO;
import com.thymeleaf.dto.MenuDTO;
import com.thymeleaf.service.ICategoryService;
import com.thymeleaf.service.IHistoryService;
import com.thymeleaf.service.IProductService;
import com.thymeleaf.utils.Constant;
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
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/history")
public class HistoryController {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IHistoryService historyService;

    @GetMapping
    public String showCategory(@ModelAttribute("histories") HistoryInput input,
                               @PageableDefault(size = 5) Pageable pageable,
                               Model model, HttpSession session) {
        HistoryOutput result = new HistoryOutput();
        input.setKeyword(input.getKeyword().trim());
        result.setKeyword(input.getKeyword());
        result.setPage(pageable.getPageNumber() + 1);
        result.setSize(pageable.getPageSize());
        result.setCategoryCode(input.getCategoryCode());
        result.setType(input.getType());
        Page<HistoryDTO> page;
        if (!input.getCategoryCode().equals("") || !input.getType().equals(0)){
            if (input.getCategoryCode().equals("")){
                input.setCategoryCode(null);
            }
            if (input.getType().equals(0)){
                input.setType(null);
            }
            page = historyService.findWithDynamicFilters(input.getKeyword(), input.getCategoryCode(), input.getType(), pageable);
        } else if (!input.getKeyword().equals("")){
            page = historyService.findByProduct_NameContaining(input.getKeyword(), pageable);
        } else {
            page = historyService.findAll(pageable);
        }
        List<HistoryDTO> historyDTOS = page.getContent();
        for (HistoryDTO dto : historyDTOS){
            dto.setProductInfo(productService.findById(dto.getProductId()));
            dto.getProductInfo().setCategory(categoryService.findById(dto.getProductInfo().getCategoryId()));
        }
        result.setListResult(historyDTOS);
        result.setTotalPage(page.getTotalPages());
        List<CategoryDTO> categories = categoryService.findAll();
        List<MenuDTO> menuList = (List<MenuDTO>) session.getAttribute(Constant.MENUS);
        UserDetails user = (UserDetails) session.getAttribute(Constant.USER);
        model.addAttribute(Constant.MENUS, menuList);
        model.addAttribute(Constant.USER, user);
        model.addAttribute(Constant.HISTORIES, result);
        model.addAttribute(Constant.CATEGORIES, categories);
        return "/fragments/contents/history/list";
    }

}
