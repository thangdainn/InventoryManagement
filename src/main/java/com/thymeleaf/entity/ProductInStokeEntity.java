package com.thymeleaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_in_stoke")
public class ProductInStokeEntity extends BaseEntity{

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductInfoEntity product;

    @NotNull
    @Column(name = "qty")
    private Integer qty;

    @NotNull
    @Column(name = "price")
    private Integer price;
}
