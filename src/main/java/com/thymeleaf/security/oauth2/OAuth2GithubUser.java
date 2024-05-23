package com.thymeleaf.security.oauth2;

import java.util.Map;

public class OAuth2GithubUser extends OAuth2UserDetail{

    public OAuth2GithubUser(Map<String, Object> attributes) {
        super(attributes);
    }
    @Override
    public String getName() {
        return attributes.get("login").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("login").toString();
    }
}
