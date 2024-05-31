package com.thymeleaf.service.impl;

import com.thymeleaf.converter.AuthConverter;
import com.thymeleaf.dto.AuthDTO;
import com.thymeleaf.entity.AuthEntity;
import com.thymeleaf.repository.IAuthRepository;
import com.thymeleaf.repository.IMenuRepository;
import com.thymeleaf.repository.IRoleRepository;
import com.thymeleaf.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final IAuthRepository authRepository;

    private final AuthConverter authConverter;

    private final IRoleRepository roleRepository;

    private final IMenuRepository menuRepository;

    @Override
    public List<AuthDTO> findByRole_Id(Integer id) {
        List<AuthEntity> auths = authRepository.findByRole_Id(id);
        return auths.stream().map(auth -> {
            return authConverter.toDTO(auth);
        }).collect(Collectors.toList());
    }

    @Override
    public List<AuthDTO> findByMenu_Id(Integer id) {
        List<AuthEntity> auths = authRepository.findByMenu_Id(id);
        return auths.stream().map(auth -> {
            return authConverter.toDTO(auth);
        }).collect(Collectors.toList());
    }

    @Override
    public AuthDTO save(AuthDTO dto) {
        AuthEntity authEntity;
        Optional<AuthEntity> optional = authRepository.findByRole_IdAndMenu_Id(dto.getRoleId(), dto.getMenuId());
        if (optional.isPresent()){
            AuthEntity old = optional.get();
            authEntity = authConverter.toEntity(dto, old);
        } else {
            authEntity = authConverter.toEntity(dto);
            authEntity.setRole(roleRepository.findById(dto.getRoleId()).get());
            authEntity.setMenu(menuRepository.findById(dto.getMenuId()).get());
        }
        authEntity = authRepository.save(authEntity);
        return authConverter.toDTO(authEntity);
    }

}
