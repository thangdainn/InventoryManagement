package com.thymeleaf.security.oauth2;

import com.thymeleaf.entity.RoleEntity;
import com.thymeleaf.entity.UserEntity;
import com.thymeleaf.repository.IRoleRepository;
import com.thymeleaf.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("Loading user for client registration: {}", userRequest.getClientRegistration().getRegistrationId());
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return checkOAuth2User(userRequest, oAuth2User);
        } catch (OAuth2AuthenticationException e) {
            throw e;
        } catch (Exception e1) {
            log.info("Error: {}", e1.getMessage());
            throw new OAuth2AuthenticationException(e1.getMessage());
        }
    }

    private OAuth2User checkOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
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
        return new CustomOAuth2UserDetail(
                userDetail.getUserName(),
                userDetail.getPassword(),
                userDetail.getName(),
                userDetail.getRoles().stream().map(
                                r -> new SimpleGrantedAuthority(r.getName()))
                        .collect(Collectors.toList()));
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
}
