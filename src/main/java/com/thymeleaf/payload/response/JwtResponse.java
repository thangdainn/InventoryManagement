package com.thymeleaf.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {
    private String access_token;
    private String type = "Bearer";
    private String user_name;
    private List<String> roles;

    public JwtResponse(String access_token, String userName, List<String> roles) {
        this.access_token = access_token;
        this.user_name = userName;
        this.roles = roles;
    }
}
