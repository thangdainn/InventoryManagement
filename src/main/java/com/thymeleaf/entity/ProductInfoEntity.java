package com.thymeleaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_info")
public class ProductInfoEntity extends BaseEntity{

    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @NotBlank
    @NotNull
    @Column(name = "name")
    private String name;

    @NotBlank
    @NotNull
    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @NotBlank
    @NotNull
    @Column(name = "img_url")
    private String imgUrl;

    @OneToMany(mappedBy = "product")
    private List<HistoryEntity> histories = new ArrayList<>();

    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY)
    private ProductInStokeEntity productInStoke;

    @OneToMany(mappedBy = "product")
    private List<InvoiceEntity> invoices = new ArrayList<>();

}
