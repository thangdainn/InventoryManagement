package com.thymeleaf.api;

import com.thymeleaf.api.request.TokenInput;
import com.thymeleaf.dto.RefreshTokenDTO;
import com.thymeleaf.dto.RoleDTO;
import com.thymeleaf.dto.TokenDTO;
import com.thymeleaf.dto.UserDTO;
import com.thymeleaf.jwt.JwtTokenProvider;
import com.thymeleaf.payload.response.JwtResponse;
import com.thymeleaf.security.CustomUserDetail;
import com.thymeleaf.service.IRoleService;
import com.thymeleaf.service.ITokenService;
import com.thymeleaf.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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


//    @GetMapping("/token")
//    public ResponseEntity<?> getToken(@AuthenticationPrincipal OAuth2User oAuth2User) {
//        Authentication authentication = (Authentication) oAuth2User;
//        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
//
//        return ResponseEntity.ok(tokenProvider.generateToken(customUserDetail));
//    }
    @PostMapping(value = "/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenDTO dto) {
        TokenDTO tokenDTO = tokenService.findByRefreshToken(dto.getRefreshToken());
        if (tokenDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is not valid.");
        }
        if (tokenDTO.getRefreshTokenExpirationDate().getTime() < System.currentTimeMillis()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is expired.");
        }
        Integer tokenId = tokenDTO.getId();
        UserDTO userDTO = userService.loadUserByRefreshToken(dto.getRefreshToken());
        tokenDTO = tokenProvider.generateToken(userDTO.getUserName(), userDTO.getProviderId());
//        UserDTO userDTO = userService.findByUserName(userDTO.getUsername(), 1);
        tokenDTO.setUserId(userDTO.getId());
        tokenDTO.setId(tokenId);
        tokenService.save(tokenDTO);
        List<String> roles = roleService.findByUser_Id(userDTO.getId()).stream().map(RoleDTO::getName).collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(tokenDTO.getToken(), tokenDTO.getRefreshToken(), userDTO.getUserName(), roles));
    }


    @DeleteMapping("/token")
    public ResponseEntity<?> delete(@RequestBody TokenInput input) {
        tokenService.delete(input.getUserId());
        return ResponseEntity.ok("Delete token success.");
    }

}
