package com.thymeleaf.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private String userName;
    private String name;
    private List<String> roles;

    public JwtResponse(String token, String refreshToken, String userName, String name, List<String> roles) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.userName = userName;
        this.name = name;
        this.roles = roles;
    }
}
