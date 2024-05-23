package com.thymeleaf.security.oauth2;

import com.thymeleaf.entity.RoleEntity;
import com.thymeleaf.entity.UserEntity;
import com.thymeleaf.repository.IRoleRepository;
import com.thymeleaf.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.converter.ClaimTypeConverter;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.security.Security;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
//@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRoleRepository roleRepository;
    private static final Converter<Map<String, Object>, Map<String, Object>> DEFAULT_CLAIM_TYPE_CONVERTER = new ClaimTypeConverter(createDefaultClaimTypeConverters());

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService = new DefaultOAuth2UserService();
    private Function<ClientRegistration, Converter<Map<String, Object>, Map<String, Object>>> claimTypeConverterFactory = (clientRegistration) -> {
        return DEFAULT_CLAIM_TYPE_CONVERTER;
    };

    public CustomOidcUserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        log.info("Loading user for client registration: {}", userRequest.getClientRegistration().getRegistrationId());


        OidcUser oidcUser = super.loadUser(userRequest);

        try {
            return checkOAuth2User(userRequest, oidcUser);
        } catch (OAuth2AuthenticationException e) {
            throw e;
        } catch (Exception e1) {
            log.error(e1.getMessage());
            throw new OAuth2AuthenticationException(e1.getMessage());
        }
    }

    private OidcUser checkOAuth2User(OidcUserRequest userRequest, OidcUser oAuth2User) {
        OAuth2UserDetail oAuth2UserDetail =
                OAuth2UserDetailFactory.getOAuth2UserDetail(
                        userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if (ObjectUtils.isEmpty(oAuth2UserDetail)) {
            throw new OAuth2AuthenticationException("OAuth2UserDetail is null");
        }
        Optional<UserEntity> userEntity = userRepository.findByUserNameAndProviderId(
                oAuth2UserDetail.getEmail(), userRequest.getClientRegistration().getRegistrationId());
        UserEntity userDetail;
        Authentication authentication;
        if (userEntity.isPresent()) {
            userDetail = userEntity.get();
            if (!userDetail.getProviderId().equals(userRequest.getClientRegistration().getRegistrationId())) {
                throw new OAuth2AuthenticationException("ProviderId is not match");
            }
            authentication = new UsernamePasswordAuthenticationToken(oAuth2User, null, userDetail.getRoles().stream().map(
                    r -> new SimpleGrantedAuthority(r.getName()))
                    .collect(Collectors.toList()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            userDetail = updateOAuth2(userDetail, oAuth2UserDetail);
        } else {
            authentication = new UsernamePasswordAuthenticationToken(oAuth2User, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            userDetail = registerOAuth2(userRequest, oAuth2UserDetail);
        }
        OidcIdToken idToken = oAuth2User.getIdToken();
        OAuth2User oauth2User = this.oauth2UserService.loadUser(userRequest);
        Map<String, Object> claims = this.getClaims(userRequest, oauth2User);
        OidcUserInfo userInfo = new OidcUserInfo(claims);
//        if (userInfo == null) {
//            Map<String, Object> claims = new HashMap<>();
//            claims.put("email", oAuth2UserDetail.getEmail());
//            claims.put("name", oAuth2UserDetail.getName());
//            userInfo = new OidcUserInfo(claims);
//        }
        return new CustomOidcUser(
                userDetail.getUserName(),
                userDetail.getPassword(),
                userDetail.getName(),
                userDetail.getRoles().stream().map(
                r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList()),
                idToken,
                userInfo);
    }

    private UserEntity registerOAuth2(OAuth2UserRequest oAuth2UserRequest, OAuth2UserDetail oAuth2UserDetail){
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(oAuth2UserDetail.getEmail());
        userEntity.setName(oAuth2UserDetail.getName());
        userEntity.setProviderId(oAuth2UserRequest.getClientRegistration().getRegistrationId());
        List<RoleEntity> roles = new ArrayList<>();
        roles.add(roleRepository.findByName("ROLE_EMPLOYEE"));
        userEntity.setRoles(roles);
        return userRepository.save(userEntity);
    }

    private UserEntity updateOAuth2(UserEntity user, OAuth2UserDetail oAuth2UserDetail){
        user.setUserName(oAuth2UserDetail.getEmail());
        return userRepository.save(user);
    }

    private Map<String, Object> getClaims(OidcUserRequest userRequest, OAuth2User oauth2User) {
        Converter<Map<String, Object>, Map<String, Object>> converter = (Converter)this.claimTypeConverterFactory.apply(userRequest.getClientRegistration());
        return converter != null ? (Map)converter.convert(oauth2User.getAttributes()) : (Map)DEFAULT_CLAIM_TYPE_CONVERTER.convert(oauth2User.getAttributes());
    }
}
