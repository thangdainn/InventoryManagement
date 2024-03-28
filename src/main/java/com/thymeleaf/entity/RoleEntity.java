package com.thymeleaf.entity;

import jakarta.persistence.*;
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
//@Data

@Entity
@Table(name = "role")
public class RoleEntity extends BaseEntity{

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "roles")
    private List<UserEntity> users = new ArrayList<>();

    @OneToMany(mappedBy = "role")
    private List<AuthEntity> auths = new ArrayList<>();
}


