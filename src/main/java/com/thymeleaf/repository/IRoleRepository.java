package com.thymeleaf.repository;

import com.thymeleaf.entity.RoleEntity;
import com.thymeleaf.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IRoleRepository extends JpaRepository<RoleEntity, Integer> {
    RoleEntity findByName(String name);
    List<RoleEntity> findByUsers(UserEntity user);
    Page<RoleEntity> findByNameContaining(@Param("keyword") String name, Pageable pageable);
}
