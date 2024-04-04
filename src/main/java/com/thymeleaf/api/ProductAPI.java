package com.thymeleaf.api;

import com.thymeleaf.api.input.ProductInput;
import com.thymeleaf.api.output.CategoryOutput;
import com.thymeleaf.api.output.ProductOutput;
import com.thymeleaf.dto.*;
import com.thymeleaf.service.IProductService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductAPI {

    @Autowired
    private IProductService productService;

    @GetMapping
    public ResponseEntity<ProductOutput> getProducts(@Valid @ModelAttribute ProductInput input) {
        ProductOutput result = new ProductOutput();

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
//            result.setCategoryCode(input.getCategoryCode());
            Page<ProductInfoDTO> pageResult;
            if (!input.getCategoryCode().equals("")){
                pageResult = productService.findWithDynamicFilters(input.getKeyword(), input.getCategoryCode(), 1, pageable);
            }else if (!input.getKeyword().equals("")){
                pageResult = productService.findByNameContaining(input.getKeyword(), 1, pageable);
            } else {
                pageResult = productService.findAll(1, pageable);
            }
            result.setListResult(pageResult.getContent());
            result.setTotalPage(pageResult.getTotalPages());
        } else {
            result.setListResult(productService.findAllByActiveFlag(input.getActiveFlag()));
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = {"/{code}"})
    public ResponseEntity<ProductInfoDTO> getProduct(@PathVariable(name = "code") String code) {
        ProductInfoDTO product = productService.findByCode(code);
        if (product == null){
            throw new RuntimeException("Product is not found");
        }
        return ResponseEntity.ok(product);
    }

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
    @PreAuthorize("hasRole('ADMIN')")
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
        return ResponseEntity.ok("Delete Successfully");
    }
}
