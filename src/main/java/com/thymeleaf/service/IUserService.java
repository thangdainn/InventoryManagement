package com.thymeleaf.service;

import com.thymeleaf.api.request.AuthRequest;
import com.thymeleaf.dto.UserDTO;
import com.thymeleaf.security.CustomUserDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    UserDTO findByUserNameAndPassword(UserDTO dto);
    UserDTO findByUserName(String username, Integer status);
    UserDTO findByUserNameAndProviderId(String username, String providerId);
    UserDTO findById(Integer id);
    UserDTO save(UserDTO dto);
    Page<UserDTO> findAll(Pageable pageable);
    List<UserDTO> findAllByActiveFlag(Integer active_flag);
    Page<UserDTO> findByNameContaining(String keyword, Pageable pageable);
    void delete(Integer[] ids);

    UserDTO loadUserByRefreshToken(String refreshToken);

}
