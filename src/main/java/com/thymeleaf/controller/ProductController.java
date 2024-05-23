package com.thymeleaf.controller;

import com.thymeleaf.api.request.ProductInput;
import com.thymeleaf.api.response.ProductOutput;
import com.thymeleaf.dto.CategoryDTO;
import com.thymeleaf.dto.MenuDTO;
import com.thymeleaf.dto.ProductInfoDTO;
import com.thymeleaf.service.ICategoryService;
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
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IProductService productService;

    @GetMapping(value = {"/product/list"})
    public String showCategory(@ModelAttribute("products") ProductInput input,
                               @PageableDefault(size = 5) Pageable pageable,
                               Model model, HttpSession session) {
        ProductOutput result = new ProductOutput();
        input.setKeyword(input.getKeyword().trim());
        result.setKeyword(input.getKeyword());
        result.setPage(pageable.getPageNumber() + 1);
        result.setSize(pageable.getPageSize());
        result.setCategoryCode(input.getCategoryCode());
        Page<ProductInfoDTO> page;
        if (!input.getCategoryCode().equals("")){
            page = productService.findWithDynamicFilters(input.getKeyword(), input.getCategoryCode(), 1, pageable);
        }else if (!input.getKeyword().equals("")){
            page = productService.findByNameContaining(input.getKeyword(), 1, pageable);
        } else {
            page = productService.findAll(1, pageable);
        }
        result.setListResult(page.getContent());
        result.setTotalPage(page.getTotalPages());
        common(model, session, null, result);
        return "/fragments/contents/product/list";
    }

    @GetMapping(value = {"/product/edit/{code}"})
    public String showCategoryEdit(@PathVariable(name = "code") String code, Model model, HttpSession session) {
        ProductInfoDTO product = new ProductInfoDTO();
        product.setCode(code);
        product = productService.findByCode(product.getCode());
        common(model, session, product, null);
        return "/fragments/contents/product/edit";
    }

    @GetMapping(value = {"/product/add"})
    public String showCategoryEdit(Model model, HttpSession session) {
        ProductInfoDTO product = new ProductInfoDTO();
        common(model, session, product, null);
        return "/fragments/contents/product/edit";
    }

    private void common(Model model, HttpSession session, ProductInfoDTO product, ProductOutput result){
        List<CategoryDTO> categories = categoryService.findAll();
        List<MenuDTO> menuList = (List<MenuDTO>) session.getAttribute(Constant.MENUS);
        UserDetails user = (UserDetails) session.getAttribute(Constant.USER);
        if (result != null){
            model.addAttribute(Constant.PRODUCTS, result);
        }
        if (product != null){
            model.addAttribute(Constant.PRODUCT, product);
        }
        model.addAttribute(Constant.MENUS, menuList);
        model.addAttribute(Constant.USER, user);
        model.addAttribute(Constant.CATEGORIES, categories);
    }
}
