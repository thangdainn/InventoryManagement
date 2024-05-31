package com.thymeleaf.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thymeleaf.api.request.ProductInStokeInput;
import com.thymeleaf.api.response.ProductInStokeOutput;
import com.thymeleaf.dto.CategoryDTO;
import com.thymeleaf.dto.MenuDTO;
import com.thymeleaf.dto.ProductInStokeDTO;
import com.thymeleaf.service.ICategoryService;
import com.thymeleaf.service.IProductInStokeService;
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
@RequestMapping("/product-in-stoke")
public class ProductInStokeController {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IProductInStokeService productInStokeService;

    @GetMapping
    public String showCategory(@ModelAttribute("productInStokes") ProductInStokeInput input,
                               @PageableDefault(size = 5) Pageable pageable,
                               Model model, HttpSession session) throws JsonProcessingException {
        ProductInStokeOutput result = new ProductInStokeOutput();
        input.setKeyword(input.getKeyword().trim());
        result.setKeyword(input.getKeyword());
        result.setPage(pageable.getPageNumber() + 1);
        result.setSize(pageable.getPageSize());
        result.setCategoryCode(input.getCategoryCode());
        Page<ProductInStokeDTO> page;
        if (!input.getCategoryCode().equals("")){
            page = productInStokeService.findWithDynamicFilters(input.getKeyword(), input.getCategoryCode(), pageable);
        } else if (!input.getKeyword().equals("")){
            page = productInStokeService.findByProduct_NameContaining(input.getKeyword(), pageable);
        } else {
            page = productInStokeService.findAll(pageable);
        }
        List<ProductInStokeDTO> productInStokeDTOs = page.getContent();
        for (ProductInStokeDTO dto : productInStokeDTOs){
            dto.setProductInfo(productService.findById(dto.getProductId()));
            dto.getProductInfo().setCategory(categoryService.findById(dto.getProductInfo().getCategoryId()));
        }
        result.setListResult(productInStokeDTOs);
        result.setTotalPage(page.getTotalPages());
        List<CategoryDTO> categories = categoryService.findAll();
        List<MenuDTO> menuList = (List<MenuDTO>) session.getAttribute(Constant.MENUS);
        UserDetails user = (UserDetails) session.getAttribute(Constant.USER);
        model.addAttribute(Constant.MENUS, menuList);
        model.addAttribute(Constant.USER, user);
        model.addAttribute(Constant.PRODUCTINSTOKES, result);
        model.addAttribute(Constant.CATEGORIES, categories);
        return "/fragments/contents/productInStoke/list";
    }

}
