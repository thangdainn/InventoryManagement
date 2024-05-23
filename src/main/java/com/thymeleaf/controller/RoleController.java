package com.thymeleaf.controller;

import com.thymeleaf.api.request.RoleInput;
import com.thymeleaf.api.response.RoleOutput;
import com.thymeleaf.dto.MenuDTO;
import com.thymeleaf.dto.RoleDTO;
import com.thymeleaf.service.IRoleService;
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
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private IRoleService roleService;

    @GetMapping(value = {"/list"})
    public String showUser(@ModelAttribute("roles") RoleInput input,
                               @PageableDefault(size = 4) Pageable pageable,
                               Model model, HttpSession session) {
        RoleOutput result = new RoleOutput();
        input.setKeyword(input.getKeyword().trim());
        result.setPage(pageable.getPageNumber() + 1);
        result.setSize(pageable.getPageSize());
        result.setKeyword(input.getKeyword());
        Page<RoleDTO> page;
        if (!input.getKeyword().equals("")){
            page = roleService.findByNameContaining(input.getKeyword(), pageable);
        } else {
            page = roleService.findAll(pageable);
        }
        result.setListResult(page.getContent());
        result.setTotalPage(page.getTotalPages());
        List<MenuDTO> menuList = (List<MenuDTO>) session.getAttribute(Constant.MENUS);
        UserDetails user = (UserDetails) session.getAttribute(Constant.USER);
        model.addAttribute(Constant.MENUS, menuList);
        model.addAttribute(Constant.USER, user);
        model.addAttribute(Constant.ROlES, result);
        return "/fragments/contents/role/list";
    }

    @GetMapping(value = {"/edit/{id}"})
    public String showUserEdit(@PathVariable(name = "id") Integer id, Model model, HttpSession session) {
        RoleDTO dto = roleService.findById(id);
        List<MenuDTO> menuList = (List<MenuDTO>) session.getAttribute(Constant.MENUS);
        UserDetails user = (UserDetails) session.getAttribute(Constant.USER);
        model.addAttribute(Constant.MENUS, menuList);
        model.addAttribute(Constant.USER, user);
        model.addAttribute(Constant.ROlE, dto);
        return "/fragments/contents/role/edit";
    }

    @GetMapping(value = {"/add"})
    public String showUserEdit(Model model, HttpSession session) {
        RoleDTO dto = new RoleDTO();
        List<RoleDTO> roles = roleService.findAll();
        List<MenuDTO> menuList = (List<MenuDTO>) session.getAttribute(Constant.MENUS);
        UserDetails user = (UserDetails) session.getAttribute(Constant.USER);
        model.addAttribute(Constant.MENUS, menuList);
        model.addAttribute(Constant.USER, user);
        model.addAttribute(Constant.ROlE, dto);
        return "/fragments/contents/role/edit";
    }

}
