package com.thymeleaf.api;

import com.thymeleaf.dto.RoleDTO;
import com.thymeleaf.service.IRoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/role")
//@ComponentScan(basePackages = {"com.thymeleaf.api"})
public class RoleAPI {

    @Autowired
    private IRoleService roleService;

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody RoleDTO model, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }
        model = roleService.save(model);
        return ResponseEntity.ok(model);
    }

//    @PostMapping
////    @PreAuthorize("permitAll()")
//    public CategoryDTO createCategory(@RequestBody CategoryDTO model){
//        return categoryService.save(model);
//    }

    @PutMapping(value = "/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCUser(@RequestBody RoleDTO model, @PathVariable("id") Integer id,
                                            BindingResult bindingResult){
        if (model.getName() == null || model.getName().trim().isEmpty()) {
            bindingResult.addError(new FieldError("user", "name", "Name is required"));
        }
        if (bindingResult.hasErrors()){
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }
        model.setId(id);
        model = roleService.save(model);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestBody Integer[] ids, BindingResult bindingResult){
        if (ids == null || ids.length == 0){
            bindingResult.addError(new FieldError("user", "ids", "Please select item."));
        }
        if (bindingResult.hasErrors()){
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }
        roleService.delete(ids);
        return ResponseEntity.ok("");
    }
}
