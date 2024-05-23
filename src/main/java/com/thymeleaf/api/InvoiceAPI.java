package com.thymeleaf.api;

import com.thymeleaf.api.request.InvoiceInput;
import com.thymeleaf.api.response.InvoiceOutput;
import com.thymeleaf.dto.InvoiceDTO;
import com.thymeleaf.service.IInvoiceService;
import com.thymeleaf.utils.TypeInvoice;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceAPI {

    @Autowired
    private IInvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<InvoiceOutput> getInvoices(@Valid @ModelAttribute InvoiceInput input) {
        InvoiceOutput result = new InvoiceOutput();
        Integer type = checkType(input.getTypeInvoice());

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
            result.setCategoryCode(input.getCategoryCode());
            Page<InvoiceDTO> page;
            if (!input.getCategoryCode().isEmpty() || result.getFromDate() != null || result.getToDate() != null) {
                if (input.getCategoryCode().isEmpty()){
                    input.setCategoryCode(null);
                }
                page = invoiceService.findWithDynamicFilters(input.getKeyword(), input.getCategoryCode(), type,
                        result.getFromDate(), result.getToDate(), pageable);
            } else if (!input.getKeyword().isEmpty()) {
                page = invoiceService.findByTypeAndProduct_NameContaining(input.getKeyword(), type, pageable);
            } else {
                page = invoiceService.findByType(type, pageable);
            }
            result.setListResult(page.getContent());
            result.setTotalPage(page.getTotalPages());
        } else {
            result.setListResult(invoiceService.findAllByTypeAndActiveFlag(type ,input.getActiveFlag()));
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = {"/{code}"})
    public ResponseEntity<InvoiceDTO> getInvoice(@PathVariable(name = "code") String code) {
        InvoiceDTO invoice = invoiceService.findByCode(code);
        if (invoice == null) {
            throw new RuntimeException("Invoice not found");
        }
        return ResponseEntity.ok(invoice);
    }

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

    private Integer checkType(String typeInvoice) {
        if (typeInvoice.equals(TypeInvoice.GOODS_RECEIPT.toString())) {
            return 1;
        }
        return 2;
    }

}
