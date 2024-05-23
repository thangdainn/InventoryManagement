package com.thymeleaf.controller;

import com.thymeleaf.dto.MenuDTO;
import com.thymeleaf.payload.response.JwtResponse;
import com.thymeleaf.security.oauth2.OAuth2UserDetail;
import com.thymeleaf.utils.Constant;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @GetMapping(value = {"/oAuth2"})
    public String getHomePageOAuth2(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        OAuth2UserDetail oidcUser = (OAuth2UserDetail) authentication.getPrincipal();
        model.addAttribute("userAttributes", oidcUser.getAttributes());
        return "oAuth2";
    }

    @GetMapping(value = {"/auth"})
    @ResponseBody
    public Authentication getAuthentication(Authentication authentication) {
        return authentication;
    }
}
