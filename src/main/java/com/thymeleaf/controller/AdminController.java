package com.thymeleaf.controller;

import org.springframework.web.bind.annotation.GetMapping;

//@Controller
//@RequestMapping("/admin")
public class AdminController {

    @GetMapping(value = {"/home"})
    public String getHomePage(){
        return "index";
    }
}
