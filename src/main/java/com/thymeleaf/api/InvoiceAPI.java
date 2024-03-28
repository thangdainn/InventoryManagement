package com.thymeleaf.api;

import com.thymeleaf.dto.InvoiceDTO;
import com.thymeleaf.service.ICategoryService;
import com.thymeleaf.service.IInvoiceService;
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
@RequestMapping("/invoice")
public class InvoiceAPI {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IInvoiceService invoiceService;

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
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> createInvoice(@Valid @RequestBody InvoiceDTO model, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }
        model = invoiceService.save(model);
        return ResponseEntity.ok(model);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> updateCategory(@RequestBody InvoiceDTO model, @PathVariable("id") Integer id,
                                            BindingResult bindingResult){
        if (model.getProductId() == null) {
            bindingResult.addError(new FieldError("invoice", "product", "Product is required"));
        }
        if (model.getCode() == null || model.getCode().trim().isEmpty()) {
            bindingResult.addError(new FieldError("invoice", "code", "Code is required"));
        }
        if (model.getQty() == null){
            bindingResult.addError(new FieldError("invoice", "qty", "Quantity is required"));
        }
        if (model.getPrice() == null){
            bindingResult.addError(new FieldError("invoice", "price", "Price is required"));
        }
        if (bindingResult.hasErrors()){
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }
        model.setId(id);
        model = invoiceService.save(model);
        return ResponseEntity.ok(model);
    }

//    @DeleteMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> delete(@RequestBody Integer[] ids, BindingResult bindingResult){
//        if (ids == null || ids.length == 0){
//            bindingResult.addError(new FieldError("category", "ids", "Please select item."));
//        }
//        if (bindingResult.hasErrors()){
//            List<String> errorMessages = bindingResult.getAllErrors().stream()
//                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
//                    .collect(Collectors.toList());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
//        }
//
//        invoiceService.delete(ids);
//        return ResponseEntity.ok("");
//    }
}
