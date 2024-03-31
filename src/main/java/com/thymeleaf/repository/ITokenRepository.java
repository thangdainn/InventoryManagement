package com.thymeleaf.repository;

import com.thymeleaf.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITokenRepository extends JpaRepository<TokenEntity, Integer> {

    Optional<TokenEntity> findByUser_Id(Integer id);
    Optional<TokenEntity> findByRefreshToken(String refreshToken);
}
