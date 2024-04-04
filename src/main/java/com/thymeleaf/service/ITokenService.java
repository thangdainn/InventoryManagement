package com.thymeleaf.service;

import com.thymeleaf.dto.TokenDTO;
import com.thymeleaf.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITokenService {
    TokenDTO findById(Integer id);
    TokenDTO save(TokenDTO dto);
    TokenDTO findByRefreshToken(String refreshToken);
    TokenDTO findByUser_Id(Integer userId);

    void delete(Integer userId);
}
