package com.thymeleaf.api;

import com.thymeleaf.api.input.CategoryInput;
import com.thymeleaf.api.output.CategoryOutput;
import com.thymeleaf.dto.CategoryDTO;
import com.thymeleaf.dto.MenuDTO;
import com.thymeleaf.service.ICategoryService;
import com.thymeleaf.utils.Constant;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/category")
public class CategoryAPI {

    @Autowired
    private ICategoryService categoryService;

    @GetMapping
    public ResponseEntity<CategoryOutput> getCategories(@Valid @ModelAttribute CategoryInput input){
        CategoryOutput result = new CategoryOutput();

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
            result.setPage(pageable.getPageNumber());
            result.setSize(pageable.getPageSize());
            Page<CategoryDTO> pageResult;
            if (!input.getKeyword().equals("")) {
                pageResult = categoryService.findByNameContaining(input.getKeyword(), pageable);
            } else {
                pageResult = categoryService.findAll(pageable);
            }
            result.setListResult(pageResult.getContent());
            result.setTotalPage(pageResult.getTotalPages());
        } else {
            result.setListResult(categoryService.findAllByActiveFlag(input.getActiveFlag()));
        }


        return ResponseEntity.ok(result);
    }

    @GetMapping(value = {"/{code}"})
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable(name = "code") String code) {
        CategoryDTO category = categoryService.findByCode(code);
        if (category == null) {
            throw new RuntimeException("Category not found");
        }
        return ResponseEntity.ok(category);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }
        model = categoryService.save(model);
        return ResponseEntity.ok(model);
    }


    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@RequestBody CategoryDTO model, @PathVariable("id") Integer id,
                                            BindingResult bindingResult) {
        if (model.getName() == null || model.getName().trim().isEmpty()) {
            bindingResult.addError(new FieldError("category", "name", "Name is required"));
        }
        if (model.getCode() == null || model.getCode().trim().isEmpty()) {
            bindingResult.addError(new FieldError("category", "code", "Code is required"));
        }
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }
        model.setId(id);
        model = categoryService.save(model);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@RequestBody Integer[] ids, BindingResult bindingResult) {
        if (ids == null || ids.length == 0) {
            bindingResult.addError(new FieldError("category", "ids", "Please select item."));
        }
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }
        categoryService.delete(ids);
        return ResponseEntity.ok("Delete categories Successfully");
    }
}
