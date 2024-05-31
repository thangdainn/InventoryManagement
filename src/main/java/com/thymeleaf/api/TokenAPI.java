package com.thymeleaf.api;

import com.thymeleaf.api.request.TokenInput;
import com.thymeleaf.dto.RoleDTO;
import com.thymeleaf.dto.TokenDTO;
import com.thymeleaf.dto.UserDTO;
import com.thymeleaf.jwt.JwtTokenProvider;
import com.thymeleaf.jwt.JwtResponse;
import com.thymeleaf.service.IRoleService;
import com.thymeleaf.service.ITokenService;
import com.thymeleaf.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class TokenAPI {

    @Autowired
    private IUserService userService;

    private final IRoleService roleService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ITokenService tokenService;

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String bearerRefreshToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(bearerRefreshToken) || !bearerRefreshToken.startsWith("Bearer ")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authorization header is required");
        }
        String refreshToken = bearerRefreshToken.substring(7);
        TokenDTO tokenDTO = tokenService.findByRefreshToken(refreshToken);
        if (tokenDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is not valid.");
        }
        if (tokenDTO.getRefreshTokenExpirationDate().getTime() < System.currentTimeMillis()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is expired.");
        }
        Integer tokenId = tokenDTO.getId();
        UserDTO userDTO = userService.loadUserByRefreshToken(refreshToken);
        tokenDTO = tokenProvider.generateToken(userDTO.getUserName(), userDTO.getProviderId(), response);
//        UserDTO userDTO = userService.findByUserName(userDTO.getUsername(), 1);
        tokenDTO.setUserId(userDTO.getId());
        tokenDTO.setId(tokenId);
        tokenService.save(tokenDTO);
        List<String> roles = roleService.findByUser_Id(userDTO.getId()).stream().map(RoleDTO::getName).collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(tokenDTO.getToken(), userDTO.getUserName(), roles));
    }


    @DeleteMapping("/token")
    public ResponseEntity<?> delete(@RequestBody TokenInput input) {
        tokenService.delete(input.getUserId());
        return ResponseEntity.ok("Delete token success.");
    }

}
