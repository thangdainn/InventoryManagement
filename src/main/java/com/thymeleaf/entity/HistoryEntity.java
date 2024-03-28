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
@Table(name = "history")
public class HistoryEntity extends BaseEntity{

    @Column(name = "action_name")
    private String actionName;

    @Column(name = "type")
    private Integer type;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductInfoEntity product;

    @Column(name = "qty")
    private Integer qty;

    @Column(name = "price")
    private Integer price;
}
