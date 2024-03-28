package com.thymeleaf.service.impl;

import com.thymeleaf.converter.RoleConverter;
import com.thymeleaf.dto.RoleDTO;
import com.thymeleaf.entity.RoleEntity;
import com.thymeleaf.repository.IRoleRepository;
import com.thymeleaf.repository.IUserRepository;
import com.thymeleaf.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService implements IRoleService {

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private RoleConverter roleConverter;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public RoleDTO findByName(RoleDTO dto) {
        return roleConverter.toDTO(roleRepository.findByName(dto.getName()));
    }

    @Override
    public List<RoleDTO> findAll() {
        List<RoleEntity> entities = roleRepository.findAll();
        return entities.stream().map(role -> {
            return roleConverter.toDTO(role);
        }).collect(Collectors.toList());
    }

    @Override
    public List<RoleDTO> findByUser_Id(Integer id) {
        List<RoleEntity> entities = roleRepository.findByUsers(userRepository.findById(id).get());
        return entities.stream().map(role -> {
            return roleConverter.toDTO(role);
        }).collect(Collectors.toList());
    }

    @Override
    public RoleDTO save(RoleDTO dto) {
        RoleEntity roleEntity = new RoleEntity();
        if (dto.getId() != null) {
            Optional<RoleEntity> optional = roleRepository.findById(dto.getId());
            if (optional.isPresent()) {
                RoleEntity old = optional.get();
                roleEntity = roleConverter.toEntity(dto, old);
            }
        } else {
            roleEntity = roleConverter.toEntity(dto);
        }
        roleEntity = roleRepository.save(roleEntity);
        return roleConverter.toDTO(roleEntity);
    }

    @Override
    public Page<RoleDTO> findAll(Pageable pageable) {
        Page<RoleEntity> entityPage = roleRepository.findAll(pageable);
        List<RoleDTO> dtoList = entityPage.stream().map(role -> {
            return roleConverter.toDTO(role);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    public Page<RoleDTO> findByNameContaining(String keyword, Pageable pageable) {
        Page<RoleEntity> entityPage = roleRepository.findByNameContaining(keyword, pageable);
        List<RoleDTO> dtoList = entityPage.stream().map(role -> {
            return roleConverter.toDTO(role);
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    public RoleDTO findById(Integer id) {
        Optional<RoleEntity> optional = roleRepository.findById(id);
        if (optional.isPresent()) {
            return roleConverter.toDTO(optional.get());
        }
        return null;
    }

    @Override
    public void delete(Integer[] ids) {
        roleRepository.deleteAllByIdInBatch(Arrays.asList(ids));

    }
}
