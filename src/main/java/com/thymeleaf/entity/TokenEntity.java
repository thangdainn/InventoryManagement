package com.thymeleaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "token")
public class TokenEntity extends BaseEntity{

//    @NotNull
//    @Column(name = "access_token")
//    private String token;

//    @NotNull
//    @Column(name = "expiration_date")
//    private Timestamp expirationDate;

    @NotNull
    @Column(name = "refresh_token")
    private String refreshToken;

    @NotNull
    @Column(name = "refresh_token_expiration_date")
    private Timestamp refreshTokenExpirationDate;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
