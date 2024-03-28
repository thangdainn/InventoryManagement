package com.thymeleaf.controller;

import com.thymeleaf.api.input.UserInput;
import com.thymeleaf.api.output.MenuOutput;
import com.thymeleaf.dto.AuthDTO;
import com.thymeleaf.dto.MenuDTO;
import com.thymeleaf.dto.RoleDTO;
import com.thymeleaf.service.IAuthService;
import com.thymeleaf.service.IMenuService;
import com.thymeleaf.service.IRoleService;
import com.thymeleaf.utils.Constant;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Controller
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private IMenuService menuService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IAuthService authService;

    @GetMapping(value = {"/list"})
    public String showUser(@ModelAttribute("menupage") UserInput input,
                               @PageableDefault(size = 10) Pageable pageable,
                               Model model, HttpSession session) {
        MenuOutput result = new MenuOutput();
        result.setPage(pageable.getPageNumber() + 1);
        result.setSize(pageable.getPageSize());
        Page<MenuDTO> page = menuService.findAll(pageable);
        List<RoleDTO> roles = roleService.findAll();
        List<MenuDTO> menus = new ArrayList<>(page.getContent());
        menus.sort((o1, o2) -> o1.getId() - o2.getId());
        for (MenuDTO menu : menus){
            menu.setAuths(authService.findByMenu_Id(menu.getId()));
            Map<Integer, Integer> mapAuth = new TreeMap<>();
            for (RoleDTO role : roles){
                mapAuth.put(role.getId(), 0);
            }
            for (AuthDTO auth : menu.getAuths()){
//                AuthDTO auth = (AuthDTO) obj;
                mapAuth.put(auth.getRoleId(), auth.getPermission());
            }
            menu.setMapAuth(mapAuth);
        }

        result.setListResult(menus);
        result.setTotalPage(page.getTotalPages());
        List<MenuDTO> menuList = (List<MenuDTO>) session.getAttribute(Constant.MENUS);
        UserDetails user = (UserDetails) session.getAttribute(Constant.USER);
        model.addAttribute(Constant.MENUS, menuList);
        model.addAttribute(Constant.USER, user);
        model.addAttribute(Constant.MENUPAGE, result);
        model.addAttribute(Constant.ROlES, roles);
        return "/fragments/contents/menu/list";
    }

    @PostMapping(value = {"/change-permission/{id}"})
    public String changPermission(@PathVariable(name = "id") Integer id) {
        MenuDTO menu = menuService.findById(id);
        menu.setActiveFlag(menu.getActiveFlag() == 0 ? 1 : 0);
        menuService.save(menu);
        return "redirect:/menu/list?success";
    }

    @GetMapping(value = {"/permission"})
    public String showPermission(Model model, HttpSession session) {
        AuthDTO auth = new AuthDTO();
        auth.setPermission(1);
        List<MenuDTO> menus = menuService.findByActiveFlag(1);
        List<RoleDTO> roles = roleService.findAll();
        List<MenuDTO> menuList = (List<MenuDTO>) session.getAttribute(Constant.MENUS);
        UserDetails user = (UserDetails) session.getAttribute(Constant.USER);
        model.addAttribute(Constant.MENUS, menuList);
        model.addAttribute(Constant.USER, user);
        model.addAttribute(Constant.AUTH, auth);
        model.addAttribute(Constant.MENUPAGE, menus);
        model.addAttribute(Constant.ROlES, roles);
        return "/fragments/contents/menu/permission";
    }

    @PostMapping(value = {"/setAuth"})
    public ResponseEntity<?> setAuth(@RequestBody AuthDTO model) {
        model = authService.save(model);
        return ResponseEntity.ok(model);
    }
}
