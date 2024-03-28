package com.thymeleaf.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "menu")
public class MenuEntity extends BaseEntity{

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "url")
    private String url;

    @Column(name = "name")
    private String name;

    @Column(name = "order_index")
    private Integer orderIndex;

    @OneToMany(mappedBy = "menu")
    private List<AuthEntity> auths = new ArrayList<>();
}
