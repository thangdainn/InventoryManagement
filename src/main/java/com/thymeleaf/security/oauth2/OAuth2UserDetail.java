package com.thymeleaf.security.oauth2;

import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class OAuth2UserDetail {
    protected Map<String, Object> attributes;
    public abstract String getName();
    public abstract String getEmail();
}
