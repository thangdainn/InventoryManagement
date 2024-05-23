package com.thymeleaf.controller;

import com.thymeleaf.api.request.UserInput;
import com.thymeleaf.api.response.UserOutput;
import com.thymeleaf.dto.MenuDTO;
import com.thymeleaf.dto.RoleDTO;
import com.thymeleaf.dto.UserDTO;
import com.thymeleaf.service.IRoleService;
import com.thymeleaf.service.IUserService;
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
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @GetMapping(value = {"/list"})
    public String showUser(@ModelAttribute("users") UserInput input,
                               @PageableDefault(size = 4) Pageable pageable,
                               Model model, HttpSession session) {
        UserOutput result = new UserOutput();
        input.setKeyword(input.getKeyword().trim());
        result.setPage(pageable.getPageNumber() + 1);
        result.setSize(pageable.getPageSize());
        result.setKeyword(input.getKeyword());
        Page<UserDTO> page;
        if (!input.getKeyword().equals("")){
            page = userService.findByNameContaining(input.getKeyword(), pageable);
        } else {
            page = userService.findAll(pageable);
        }
        for (UserDTO userDTO : page.getContent()){
            userDTO.setRoles(roleService.findByUser_Id(userDTO.getId()));
        }
        result.setListResult(page.getContent());
        result.setTotalPage(page.getTotalPages());
        List<MenuDTO> menuList = (List<MenuDTO>) session.getAttribute(Constant.MENUS);
        UserDetails user = (UserDetails) session.getAttribute(Constant.USER);
        model.addAttribute(Constant.MENUS, menuList);
        model.addAttribute(Constant.USER, user);
        model.addAttribute(Constant.USERS, result);
        return "/fragments/contents/user/list";
    }

    @GetMapping(value = {"/edit/{id}"})
    public String showUserEdit(@PathVariable(name = "id") Integer id, Model model, HttpSession session) {
        UserDTO dto = userService.findById(id);
        dto.setRoles(roleService.findByUser_Id(id));
        List<RoleDTO> roles = roleService.findAll();
        List<MenuDTO> menuList = (List<MenuDTO>) session.getAttribute(Constant.MENUS);
        UserDetails user = (UserDetails) session.getAttribute(Constant.USER);
        model.addAttribute(Constant.MENUS, menuList);
        model.addAttribute(Constant.USER, user);
        model.addAttribute(Constant.USERDATA, dto);
        model.addAttribute(Constant.ROlES, roles);
        return "/fragments/contents/user/edit";
    }

    @GetMapping(value = {"/add"})
    public String showUserEdit(Model model, HttpSession session) {
        UserDTO dto = new UserDTO();
        List<RoleDTO> roles = roleService.findAll();
        List<MenuDTO> menuList = (List<MenuDTO>) session.getAttribute(Constant.MENUS);
        UserDetails user = (UserDetails) session.getAttribute(Constant.USER);
        model.addAttribute(Constant.MENUS, menuList);
        model.addAttribute(Constant.USER, user);
        model.addAttribute(Constant.USERDATA, dto);
        model.addAttribute(Constant.ROlES, roles);
        return "/fragments/contents/user/edit";
    }

}
