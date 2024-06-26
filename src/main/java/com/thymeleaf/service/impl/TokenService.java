package com.thymeleaf.service.impl;

import com.thymeleaf.converter.TokenConverter;
import com.thymeleaf.dto.TokenDTO;
import com.thymeleaf.entity.TokenEntity;
import com.thymeleaf.entity.UserEntity;
import com.thymeleaf.repository.ITokenRepository;
import com.thymeleaf.repository.IUserRepository;
import com.thymeleaf.service.ITokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {

    private final ITokenRepository tokenRepository;

    private final TokenConverter tokenConverter;

    private final IUserRepository userRepository;

    @Override
    public TokenDTO findById(Integer id) {
        Optional<TokenEntity> entities = tokenRepository.findById(id);
        return entities.map(entity -> tokenConverter.toDTO(entity)).orElse(null);
    }

    @Override
    public TokenDTO save(TokenDTO dto) {
        TokenEntity tokenEntity = new TokenEntity();
        if (dto.getId() != null) {
            Optional<TokenEntity> optional = tokenRepository.findById(dto.getId());
            if (optional.isPresent()) {
                TokenEntity oldToken = optional.get();
                tokenEntity = tokenConverter.toEntity(dto, oldToken);
            }
        } else {
            tokenEntity = tokenConverter.toEntity(dto);
        }
        Optional<UserEntity> optionalUser = userRepository.findById(dto.getUserId());
        if (optionalUser.isPresent()) {
            tokenEntity.setUser(optionalUser.get());
        }
        tokenEntity = tokenRepository.save(tokenEntity);
        return tokenConverter.toDTO(tokenEntity);
    }

    @Override
    public TokenDTO findByRefreshToken(String refreshToken) {
        Optional<TokenEntity> optional = tokenRepository.findByRefreshToken(refreshToken);
        return optional.map(entity -> tokenConverter.toDTO(entity)).orElse(null);
    }

    @Override
    public TokenDTO findByUser_Id(Integer userId) {
        Optional<TokenEntity> optional = tokenRepository.findByUser_Id(userId);
        return optional.map(entity -> tokenConverter.toDTO(entity)).orElse(null);
    }

    @Override
    @Transactional
    public void delete(Integer userId) {
        tokenRepository.deleteByUser_Id(userId);
    }

}
