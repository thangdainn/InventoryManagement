package com.thymeleaf.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invoice")
public class InvoiceEntity extends BaseEntity{

    @Column(name = "code")
    private String code;

    @Column(name = "type")
    private Integer type;

    @Column(name = "qty")
    private Integer qty;

    @Column(name = "price")
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductInfoEntity product;


}
