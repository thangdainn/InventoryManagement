package com.thymeleaf.repository;

import com.thymeleaf.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.Optional;

public interface ITokenRepository extends JpaRepository<TokenEntity, Integer> {

    Optional<TokenEntity> findByUser_Id(Integer id);
    Optional<TokenEntity> findByRefreshToken(String refreshToken);

    void deleteByUser_Id(Integer userId);
    void deleteByRefreshTokenExpirationDateBefore(Timestamp date);
    void deleteByRefreshToken(String refreshToken);
}
