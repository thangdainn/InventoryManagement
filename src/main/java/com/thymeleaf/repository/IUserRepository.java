package com.thymeleaf.repository;

import com.thymeleaf.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IUserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUserNameAndPasswordAndActiveFlag(String userName, String password, Integer activeFlag);
    Optional<UserEntity> findByUserNameAndActiveFlag(String userName, Integer activeFlag);

    Optional<UserEntity> findByUserNameAndProviderId(String userName, String providerId);
    Page<UserEntity> findByNameContaining(@Param("keyword") String name, Pageable pageable);

    List<UserEntity> findAllByActiveFlag(Integer activeFlag);
}
