package com.thymeleaf.security;

import com.thymeleaf.entity.UserEntity;
import com.thymeleaf.repository.IRoleRepository;
import com.thymeleaf.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntities = userRepository.findByUserNameAndActiveFlag(username, 1);
        if (userEntities.isPresent()) {
            UserEntity user = userEntities.get();
            user.setRoles(roleRepository.findByUsers(user));
            return new CustomUserDetail(user);
        } else {
            return new CustomUserDetail(username, "", "", new ArrayList<>());
        }
    }
    public UserDetails loadUserByUsernameAndProviderId(String username, String providerId) throws UsernameNotFoundException {
        Optional<UserEntity> userEntities = userRepository.findByUserNameAndProviderId(username, providerId);
        if (userEntities.isPresent()) {
            UserEntity user = userEntities.get();
            user.setRoles(roleRepository.findByUsers(user));
            return new CustomUserDetail(user);
        } else {
            return new CustomUserDetail(username, "", "", new ArrayList<>());
        }
    }
}
