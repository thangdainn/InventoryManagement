package com.thymeleaf.api;

import com.thymeleaf.dto.UserDTO;
import com.thymeleaf.jwt.JwtTokenProvider;
import com.thymeleaf.payload.request.LoginRequest;
import com.thymeleaf.payload.response.JwtResponse;
import com.thymeleaf.security.CustomUserDetail;
import com.thymeleaf.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
//@RequestMapping("/auth")
public class UserAPI {

    @Autowired
    private IUserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping(value = "/signin")
    public ResponseEntity<?> login(@RequestBody LoginRequest user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        String jwt = tokenProvider.generateToken(customUserDetail);
        List<String> listRole = customUserDetail.getAuthorities().stream()
                .map(role -> role.getAuthority()).collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt, customUserDetail.getUsername(), customUserDetail.getName(), listRole));
    }

    @PostMapping(value = {"/user", "/register"})
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }
        if (checkUsername(model.getUserName())) {
            String error = "Username available";
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add(error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }
        model = userService.save(model);
        return ResponseEntity.ok(model);
    }

    @PutMapping(value = "/user/{id}")
    public ResponseEntity<?> updateCUser(@RequestBody UserDTO model, @PathVariable("id") Integer id,
                                         BindingResult bindingResult) {
        if (model.getName() == null || model.getName().trim().isEmpty()) {
            bindingResult.addError(new FieldError("user", "name", "Name is required"));
        }
        if (model.getUserName() == null || model.getUserName().trim().isEmpty()) {
            bindingResult.addError(new FieldError("user", "username", "Username is required"));
        } else if (!userService.findById(id).getUserName().equals(model.getUserName())) {
            if (checkUsername(model.getUserName())){
                bindingResult.addError(new FieldError("user", "username", "Username available"));
            }
        }

        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }
        model.setId(id);
        model = userService.save(model);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> delete(@RequestBody Integer[] ids, BindingResult bindingResult) {
        if (ids == null || ids.length == 0) {
            bindingResult.addError(new FieldError("user", "ids", "Please select item."));
        }
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }
        userService.delete(ids);
        return ResponseEntity.ok("");
    }

    private boolean checkUsername(String username) {
        return userService.findByUserName(username, 0) != null;
    }
}
