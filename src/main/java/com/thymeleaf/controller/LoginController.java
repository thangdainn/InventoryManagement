package com.thymeleaf.controller;

import com.thymeleaf.dto.UserDTO;
import com.thymeleaf.jwt.JwtTokenProvider;
import com.thymeleaf.service.IUserService;
import com.thymeleaf.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@Controller
public class LoginController {

    @Autowired
    private IUserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private boolean flagLogin = true;
    private boolean flagUsername = true;
    private boolean flagRegister = true;
    @GetMapping(value = "/login")
    public String loginPage(Model model) {
        if (!flagLogin){
            model.addAttribute("errorLogin", true);
            flagLogin = !flagLogin;
        } else if (!flagUsername){
            model.addAttribute("errorUsername", true);
            flagUsername = !flagUsername;
        } else if (!flagRegister){
            model.addAttribute("success", true);
            flagRegister = !flagRegister;
        }

        UserDTO dto = new UserDTO();
        model.addAttribute(Constant.USERDATA, dto);
        return "login";
    }

//    @PostMapping(value = "/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest user) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
//        );
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
//        String jwt = tokenProvider.generateToken(customUserDetail);
//        List<String> listRole = customUserDetail.getAuthorities().stream()
//                .map(role -> role.getAuthority()).collect(Collectors.toList());
//        return ResponseEntity.ok(new JwtResponse(jwt, customUserDetail.getUsername(), customUserDetail.getName(), listRole));
//    }

    @GetMapping(value = "/loginFail")
    public String loginFail() {
        flagLogin = false;
        return "redirect:/login";
    }

}
