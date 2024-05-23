package com.thymeleaf.service.impl;

import com.thymeleaf.api.request.AuthRequest;
import com.thymeleaf.converter.UserConverter;
import com.thymeleaf.dto.UserDTO;
import com.thymeleaf.entity.RoleEntity;
import com.thymeleaf.entity.TokenEntity;
import com.thymeleaf.entity.UserEntity;
import com.thymeleaf.repository.IRoleRepository;
import com.thymeleaf.repository.ITokenRepository;
import com.thymeleaf.repository.IUserRepository;
import com.thymeleaf.security.CustomUserDetail;
import com.thymeleaf.service.IUserService;
import com.thymeleaf.utils.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    private final UserConverter userConverter;

    private final IRoleRepository roleRepository;

    private final ITokenRepository tokenRepository;

    private final PasswordEncoder encoder;

    @Override
    public UserDTO findByUserNameAndPassword(UserDTO dto) {
        Optional<UserEntity> entities = userRepository.findByUserNameAndPasswordAndActiveFlag(dto.getUserName(), dto.getPassword(), 1);
        return entities.map(entity -> userConverter.toDTO(entity)).orElse(null);
    }

    @Override
    public UserDTO findByUserName(String username, Integer status) {
        Optional<UserEntity> entities = userRepository.findByUserNameAndActiveFlag(username, 1);
        return entities.map(entity -> userConverter.toDTO(entity)).orElse(null);
    }

    @Override
    public UserDTO findByUserNameAndProviderId(String username, String providerId) {
        Optional<UserEntity> entities = userRepository.findByUserNameAndProviderId(username, providerId);
        return entities.map(entity -> userConverter.toDTO(entity)).orElse(null);
    }

    @Override
    public UserDTO findById(Integer id) {
        Optional<UserEntity> entities = userRepository.findById(id);
        return entities.map(entity -> userConverter.toDTO(entity)).orElse(null);
    }

    @Override
    public UserDTO save(UserDTO dto) {
//        PasswordEncoder encoder = new BCryptPasswordEncoder();
        UserEntity userEntity = new UserEntity();
        if (dto.getId() != null){
            Optional<UserEntity> optional = userRepository.findById(dto.getId());
            if (optional.isPresent()){
                UserEntity oldUser = optional.get();
                if (!dto.getPassword().isEmpty()){
                    dto.setPassword(encoder.encode(dto.getPassword()));
                } else {
                    dto.setPassword(oldUser.getPassword());
                }
                userEntity = userConverter.toEntity(dto, oldUser);
            }
        } else {
            userEntity = userConverter.toEntity(dto);
            userEntity.setPassword(encoder.encode(userEntity.getPassword()));
        }
        userEntity.setProviderId(userEntity.getProviderId());
        List<RoleEntity> roles = new ArrayList<>();
        if (dto.getRoleIds() == null){
            roles.add(roleRepository.findByName("ROLE_EMPLOYEE"));
        } else {
            for (Integer roleId : dto.getRoleIds()){
                Optional<RoleEntity> optional = roleRepository.findById(roleId);
                if (optional.isPresent()){
                    roles.add(optional.get());
                }
            }
        }
        userEntity.setRoles(roles);
        userEntity = userRepository.save(userEntity);
        return userConverter.toDTO(userEntity);
    }

    @Override
    public Page<UserDTO> findAll(Pageable pageable) {
        Page<UserEntity> entityPage = userRepository.findAll(pageable);
        List<UserDTO> dtoList = entityPage.stream().map(user -> {
            return userConverter.toDTO(user);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    public List<UserDTO> findAllByActiveFlag(Integer active_flag) {
        List<UserEntity> list = userRepository.findAllByActiveFlag(active_flag);
        return list.stream().map(user -> {
            return userConverter.toDTO(user);
        }).collect(Collectors.toList());
    }

    @Override
    public Page<UserDTO> findByNameContaining(String keyword, Pageable pageable) {
        Page<UserEntity> entityPage = userRepository.findByNameContaining(keyword, pageable);
        List<UserDTO> dtoList = entityPage.stream().map(user -> {
            return userConverter.toDTO(user);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    public void delete(Integer[] ids) {
        userRepository.deleteAllByIdInBatch(Arrays.asList(ids));
    }

    @Override
    public UserDTO loadUserByRefreshToken(String refreshToken) {
        Optional<TokenEntity> optional = tokenRepository.findByRefreshToken(refreshToken);
        if (optional.isPresent()){
            TokenEntity tokenEntity = optional.get();
            UserEntity userEntity = tokenEntity.getUser();
            return userConverter.toDTO(userEntity);
        }
        return null;
    }

    @Override
    public CustomUserDetail loadUserByOAuth2(AuthRequest authRequest) {
//        Optional<UserEntity> optional = userRepository.findByProviderIdAndProviderUserId(Provider.google.name(), authRequest.getProviderUserId());
//        if (optional.isPresent()){
//            UserEntity userEntity = optional.get();
//            return new CustomUserDetail(userEntity);
//        }

        return null;
    }
}
