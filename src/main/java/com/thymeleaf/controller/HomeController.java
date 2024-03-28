package com.thymeleaf.controller;

import com.thymeleaf.dto.MenuDTO;
import com.thymeleaf.payload.response.JwtResponse;
import com.thymeleaf.utils.Constant;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @GetMapping(value = {"/", "/home"})
    public String getHomePage(Model model, HttpSession session) {
        List<MenuDTO> menuList = (List<MenuDTO>) session.getAttribute(Constant.MENUS);
        UserDetails user = (UserDetails) session.getAttribute(Constant.USER);
        JwtResponse jwtResponse = (JwtResponse) session.getAttribute(Constant.JWT);
        model.addAttribute(Constant.MENUS, menuList);
        model.addAttribute(Constant.USER, user);
        model.addAttribute(Constant.JWT, jwtResponse);
        return "/fragments/contents/body-start";
    }
}
