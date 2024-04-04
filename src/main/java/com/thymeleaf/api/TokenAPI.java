package com.thymeleaf.api;

import com.thymeleaf.api.input.TokenInput;
import com.thymeleaf.dto.RefreshTokenDTO;
import com.thymeleaf.dto.TokenDTO;
import com.thymeleaf.dto.UserDTO;
import com.thymeleaf.jwt.JwtTokenProvider;
import com.thymeleaf.payload.response.JwtResponse;
import com.thymeleaf.security.CustomUserDetail;
import com.thymeleaf.service.ITokenService;
import com.thymeleaf.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class TokenAPI {

    @Autowired
    private IUserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ITokenService tokenService;

    @PostMapping(value = "/refreshToken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenDTO dto) {
        TokenDTO tokenDTO = tokenService.findByRefreshToken(dto.getRefreshToken());
        if (tokenDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is not valid.");
        }
        if (tokenDTO.getRefreshTokenExpirationDate().getTime() < System.currentTimeMillis()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is expired.");
        }
        Integer tokenId = tokenDTO.getId();
        CustomUserDetail userDetail = userService.loadUserByRefreshToken(dto.getRefreshToken());
        tokenDTO = tokenProvider.generateToken(userDetail);
        UserDTO userDTO = userService.findByUserName(userDetail.getUsername(), 1);
        tokenDTO.setUserId(userDTO.getId());
        tokenDTO.setId(tokenId);
        tokenService.save(tokenDTO);
        List<String> listRole = userDetail.getAuthorities().stream()
                .map(role -> role.getAuthority()).collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(tokenDTO.getToken(), tokenDTO.getRefreshToken(), userDetail.getUsername(), userDetail.getName(), listRole));
    }


    @DeleteMapping("/token")
    public ResponseEntity<?> delete(@RequestBody TokenInput input) {
        tokenService.delete(input.getUserId());
        return ResponseEntity.ok("Delete token success.");
    }

}
