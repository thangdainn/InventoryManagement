package com.thymeleaf.security.oauth2;

import java.util.Map;

public class OAuth2FacebookUser extends OAuth2UserDetail{
    public OAuth2FacebookUser(Map<String, Object> attributes) {
        super(attributes);
    }
    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }
}
