package com.thymeleaf.service.impl;

import com.thymeleaf.converter.UserConverter;
import com.thymeleaf.dto.UserDTO;
import com.thymeleaf.entity.RoleEntity;
import com.thymeleaf.entity.UserEntity;
import com.thymeleaf.repository.IRoleRepository;
import com.thymeleaf.repository.IUserRepository;
import com.thymeleaf.service.IUserService;
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
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private IRoleRepository roleRepository;

    @Override
    public UserDTO findByUserNameAndPassword(UserDTO dto) {
        Optional<UserEntity> entities = userRepository.findByUserNameAndPasswordAndActiveFlag(dto.getUserName(), dto.getPassword(), 1);
//        UserEntity entity = entities.isEmpty() ? null : entities.get(0);
//        return entity == null ? null : userConverter.toDTO(entity);
        return entities.map(entity -> userConverter.toDTO(entity)).orElse(null);
    }

    @Override
    public UserDTO findByUserName(String username, Integer status) {
        Optional<UserEntity> entities = userRepository.findByUserNameAndActiveFlag(username, 1);
        return entities.map(entity -> userConverter.toDTO(entity)).orElse(null);
    }

    @Override
    public UserDTO findById(Integer id) {
        Optional<UserEntity> entities = userRepository.findById(id);
        return entities.map(entity -> userConverter.toDTO(entity)).orElse(null);
    }

    @Override
    public UserDTO save(UserDTO dto) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
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

        List<RoleEntity> roles = new ArrayList<>();
        for (Integer roleId : dto.getRoleIds()){
            Optional<RoleEntity> optional = roleRepository.findById(roleId);
            if (optional.isPresent()){
                roles.add(optional.get());
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
}