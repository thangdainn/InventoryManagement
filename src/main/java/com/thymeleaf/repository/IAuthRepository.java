package com.thymeleaf.repository;

import com.thymeleaf.entity.AuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IAuthRepository extends JpaRepository<AuthEntity, Integer> {
    List<AuthEntity> findByRole_Id(@Param("roleId") Integer roleId);
    List<AuthEntity> findByPermission(Integer permission);
    List<AuthEntity> findByMenu_Id(Integer id);
    Optional<AuthEntity> findByRole_IdAndMenu_Id(Integer roleId, Integer menuId);
}
