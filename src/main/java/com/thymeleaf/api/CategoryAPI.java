package com.thymeleaf.api;

import com.thymeleaf.dto.CategoryDTO;
import com.thymeleaf.service.ICategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/category")
//@ComponentScan(basePackages = {"com.thymeleaf.api"})
public class CategoryAPI {

    @Autowired
    private ICategoryService categoryService;

//    @GetMapping(value = "/category")
//    public CategoryOutput showCategory(@RequestParam(value = "page", required = false) Integer page,
//                                       @RequestParam(value = "limit", required = false) Integer limit) {
//        CategoryOutput result = new CategoryOutput();
//        if (page != null && limit != null) {
//            result.setPage(page);
//            Pageable pageable = PageRequest.of(page - 1, limit);
//            result.setListResult(categoryService.findAll(pageable));
//            result.setTotalPage((int) Math.ceil((double) categoryService.totalItem() / limit));
//        } else {
//            result.setListResult(categoryService.findAll());
//        }
//        return result;
//    }

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO model, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }
        model = categoryService.save(model);
        return ResponseEntity.ok(model);
    }

//    @PostMapping
////    @PreAuthorize("permitAll()")
//    public CategoryDTO createCategory(@RequestBody CategoryDTO model){
//        return categoryService.save(model);
//    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@RequestBody CategoryDTO model, @PathVariable("id") Integer id,
                                            BindingResult bindingResult){
        if (model.getName() == null || model.getName().trim().isEmpty()) {
            bindingResult.addError(new FieldError("category", "name", "Name is required"));
        }
        if (model.getCode() == null || model.getCode().trim().isEmpty()){
            bindingResult.addError(new FieldError("category", "code", "Code is required"));
        }
        if (bindingResult.hasErrors()){
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
    public ResponseEntity<?> delete(@RequestBody Integer[] ids, BindingResult bindingResult){
        if (ids == null || ids.length == 0){
            bindingResult.addError(new FieldError("category", "ids", "Please select item."));
        }
        if (bindingResult.hasErrors()){
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }
        categoryService.delete(ids);
        return ResponseEntity.ok("");
    }
}
