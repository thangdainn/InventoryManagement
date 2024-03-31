package com.thymeleaf.service;

import com.thymeleaf.dto.UserDTO;
import com.thymeleaf.security.CustomUserDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    UserDTO findByUserNameAndPassword(UserDTO dto);
    UserDTO findByUserName(String username, Integer status);
    UserDTO findById(Integer id);
    UserDTO save(UserDTO dto);
    Page<UserDTO> findAll(Pageable pageable);
    Page<UserDTO> findByNameContaining(String keyword, Pageable pageable);
    void delete(Integer[] ids);

    CustomUserDetail loadUserByRefreshToken(String refreshToken);
}
