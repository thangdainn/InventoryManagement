package com.thymeleaf.controller;

import com.thymeleaf.api.input.CategoryInput;
import com.thymeleaf.api.output.CategoryOutput;
import com.thymeleaf.dto.CategoryDTO;
import com.thymeleaf.dto.MenuDTO;
import com.thymeleaf.service.ICategoryService;
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
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @GetMapping(value = {"/list"})
    public String showCategory(@ModelAttribute("categories") CategoryInput input,
                               @PageableDefault(size = 4) Pageable pageable,
                               Model model, HttpSession session) {
        CategoryOutput result = new CategoryOutput();
        input.setKeyword(input.getKeyword().trim());
        result.setPage(pageable.getPageNumber() + 1);
        result.setSize(pageable.getPageSize());
        result.setKeyword(input.getKeyword());
        Page<CategoryDTO> page;
        if (!input.getKeyword().equals("")){
            page = categoryService.findByNameContaining(input.getKeyword(), pageable);
        } else {
            page = categoryService.findAll(pageable);
        }
        result.setListResult(page.getContent());
        result.setTotalPage(page.getTotalPages());
        List<MenuDTO> menuList = (List<MenuDTO>) session.getAttribute(Constant.MENUS);
        UserDetails user = (UserDetails) session.getAttribute(Constant.USER);
        model.addAttribute(Constant.MENUS, menuList);
        model.addAttribute(Constant.USER, user);
        model.addAttribute(Constant.CATEGORIES, result);
        return "/fragments/contents/category/list";
    }

    @GetMapping(value = {"/edit/{code}"})
    public String showCategoryEdit(@PathVariable(name = "code") String code, Model model, HttpSession session) {
        CategoryDTO category = new CategoryDTO();
        category.setCode(code);
        category = categoryService.findByCode(code);
        List<MenuDTO> menuList = (List<MenuDTO>) session.getAttribute(Constant.MENUS);
        UserDetails user = (UserDetails) session.getAttribute(Constant.USER);
        model.addAttribute(Constant.MENUS, menuList);
        model.addAttribute(Constant.USER, user);
        model.addAttribute(Constant.CATEGORY, category);
        return "/fragments/contents/category/edit";
    }

    @GetMapping(value = {"/add"})
    public String showCategoryEdit(Model model, HttpSession session) {
        CategoryDTO category = new CategoryDTO();
        List<MenuDTO> menuList = (List<MenuDTO>) session.getAttribute(Constant.MENUS);
        UserDetails user = (UserDetails) session.getAttribute(Constant.USER);
        model.addAttribute(Constant.MENUS, menuList);
        model.addAttribute(Constant.USER, user);
        model.addAttribute(Constant.CATEGORY, category);
        return "/fragments/contents/category/edit";
    }

}
