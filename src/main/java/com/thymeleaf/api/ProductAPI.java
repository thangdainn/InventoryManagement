package com.thymeleaf.api;

import com.thymeleaf.dto.ProductInfoDTO;
import com.thymeleaf.service.IProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductAPI {

    @Autowired
    private IProductService productService;

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
    public ResponseEntity<?> createProduct(@Valid @RequestPart("product") ProductInfoDTO model,
                                        @Valid @RequestPart("multipartFile") MultipartFile file) throws IOException {
        model.setMultipartFile(file);
        try{
            model = productService.save(model);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image is not upload due to error on server.");
        }
        return ResponseEntity.ok(model);
    }

    @PutMapping(value = "/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@Valid @RequestPart("product") ProductInfoDTO model,
                                        @RequestPart(value = "multipartFile", required = false) MultipartFile file,
                                        @PathVariable("id") Integer id) throws IOException {
        model.setId(id);
        model.setMultipartFile(file);
        try{
            model = productService.save(model);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image is not upload due to error on server.");
        }

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
        productService.delete(ids);
        return ResponseEntity.ok("");
    }
}
