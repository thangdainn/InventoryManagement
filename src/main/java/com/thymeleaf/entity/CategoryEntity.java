package com.thymeleaf.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "category")
public class CategoryEntity extends BaseEntity{

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

    @OneToMany(mappedBy = "category")
    private List<ProductInfoEntity> products = new ArrayList<>();
}
