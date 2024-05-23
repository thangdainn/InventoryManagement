package com.thymeleaf.security.oauth2;

import com.thymeleaf.utils.Provider;

import java.util.Map;

public class OAuth2UserDetailFactory {
    public static OAuth2UserDetail getOAuth2UserDetail(String registrationId, Map<String, Object> attributes){
        if (registrationId.equals(Provider.google.name())) {
            return new OAuth2GoogleUser(attributes);
        } else if (registrationId.equals(Provider.facebook.name())) {
            return new OAuth2FacebookUser(attributes);
        } else if (registrationId.equals(Provider.github.name())) {
            return new OAuth2GithubUser(attributes);
        } else {
            throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
        }
    }
}
