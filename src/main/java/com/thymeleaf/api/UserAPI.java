package com.thymeleaf.api;

import com.thymeleaf.api.input.UserInput;
import com.thymeleaf.api.output.UserOutput;
import com.thymeleaf.dto.MenuDTO;
import com.thymeleaf.dto.RoleDTO;
import com.thymeleaf.dto.TokenDTO;
import com.thymeleaf.dto.UserDTO;
import com.thymeleaf.jwt.JwtTokenProvider;
import com.thymeleaf.payload.request.LoginRequest;
import com.thymeleaf.payload.response.JwtResponse;
import com.thymeleaf.security.CustomUserDetail;
import com.thymeleaf.service.IRoleService;
import com.thymeleaf.service.ITokenService;
import com.thymeleaf.service.IUserService;
import com.thymeleaf.utils.Constant;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserAPI {

    @Autowired
    private IUserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ITokenService tokenService;

    @GetMapping
    public ResponseEntity<UserOutput> getUsers(@ModelAttribute UserInput input) {
        UserOutput result = new UserOutput();
        if (input.getPage() != null){
            Sort sortParameters;
            if (input.getSort() != null) {
                String[] sortSplit = input.getSort().split(",");
                String direction = sortSplit.length > 1 ? sortSplit[1] : "asc";
                sortParameters = Sort.by(Sort.Direction.fromString(direction), sortSplit[0]);
            } else {
                sortParameters = Sort.unsorted();
            }
            Pageable pageable = PageRequest.of(input.getPage(), input.getLimit() , sortParameters);

            input.setKeyword(input.getKeyword().trim());

            Page<UserDTO> page;
            if (!input.getKeyword().isEmpty()){
                page = userService.findByNameContaining(input.getKeyword(), pageable);
            } else {
                page = userService.findAll(pageable);
            }
            for (UserDTO userDTO : page.getContent()){
                userDTO.setPassword(null);
            }

            result.setPage(pageable.getPageNumber());
            result.setSize(pageable.getPageSize());
            result.setListResult(page.getContent());
            result.setTotalPage(page.getTotalPages());
        } else {
            result.setListResult(userService.findAllByActiveFlag(input.getActiveFlag()));
            for (UserDTO userDTO : result.getListResult()){
                userDTO.setPassword(null);
            }
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = {"/{id}"})
    public ResponseEntity<UserDTO> getUser(@PathVariable(name = "id") Integer id) {
        UserDTO dto = userService.findById(id);
        if (dto == null){
            throw new RuntimeException("User not found");
        }
        dto.setPassword(null);
        return ResponseEntity.ok(dto);
    }

    @PostMapping(value = "/signin")
    public ResponseEntity<?> login(@RequestBody LoginRequest user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        TokenDTO tokenDTO = tokenProvider.generateToken(customUserDetail);
        UserDTO userDTO = userService.findByUserName(user.getUserName(), 1);
        tokenDTO.setUserId(userDTO.getId());
        tokenService.save(tokenDTO);
        List<String> listRole = customUserDetail.getAuthorities().stream()
                .map(role -> role.getAuthority()).collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(tokenDTO.getToken(), tokenDTO.getRefreshToken(), customUserDetail.getUsername(), customUserDetail.getName(), listRole));
    }

    @PostMapping(value = {"/register"})
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
        model.setPassword(null);
        return ResponseEntity.ok(model);
    }

    @PutMapping(value = "/{id}")
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
        model.setPassword(null);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping
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
        return ResponseEntity.ok("Delete users successfully");
    }

    private boolean checkUsername(String username) {
        return userService.findByUserName(username, 0) != null;
    }
}
