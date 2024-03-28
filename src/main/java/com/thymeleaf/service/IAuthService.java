package com.thymeleaf.service;

import com.thymeleaf.dto.AuthDTO;

import java.util.List;

public interface IAuthService {
    List<AuthDTO> findByRole_Id(Integer id);
    List<AuthDTO> findByMenu_Id(Integer id);
    AuthDTO save(AuthDTO dto);
}
