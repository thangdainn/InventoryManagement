package com.thymeleaf.api;

import com.thymeleaf.api.input.RoleInput;
import com.thymeleaf.api.output.RoleOutput;
import com.thymeleaf.dto.MenuDTO;
import com.thymeleaf.dto.RoleDTO;
import com.thymeleaf.service.IRoleService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/role")
public class RoleAPI {

    @Autowired
    private IRoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleOutput> getRoles(@ModelAttribute RoleInput input) {
        RoleOutput result = new RoleOutput();

        if (input.getPage() != null) {
            Sort sortParameters;
            if (input.getSort() != null) {
                String[] sortSplit = input.getSort().split(",");
                String direction = sortSplit.length > 1 ? sortSplit[1] : "asc";
                sortParameters = Sort.by(Sort.Direction.fromString(direction), sortSplit[0]);
            } else {
                sortParameters = Sort.unsorted();
            }
            Pageable pageable = PageRequest.of(input.getPage(), input.getLimit(), sortParameters);

            input.setKeyword(input.getKeyword().trim());
            result.setPage(pageable.getPageNumber());
            result.setSize(pageable.getPageSize());
            Page<RoleDTO> page;
            if (!input.getKeyword().equals("")) {
                page = roleService.findByNameContaining(input.getKeyword(), pageable);
            } else {
                page = roleService.findAll(pageable);
            }
            result.setListResult(page.getContent());
            result.setTotalPage(page.getTotalPages());
        } else {
            result.setListResult(roleService.findAllByActiveFlag(input.getActiveFlag()));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = {"/{id}"})
    public ResponseEntity<RoleDTO> getRole(@PathVariable(name = "id") Integer id) {
        RoleDTO dto = roleService.findById(id);
        if (dto == null){
            throw new RuntimeException("Role not found");
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")

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

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
        return ResponseEntity.ok("Delete Successfully");
    }
}
