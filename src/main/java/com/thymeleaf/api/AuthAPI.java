package com.thymeleaf.api;

import com.thymeleaf.dto.RoleDTO;
import com.thymeleaf.dto.TokenDTO;
import com.thymeleaf.dto.UserDTO;
import com.thymeleaf.jwt.JwtTokenProvider;
import com.thymeleaf.payload.request.LoginRequest;
import com.thymeleaf.payload.response.JwtResponse;
import com.thymeleaf.security.CustomUserDetail;
import com.thymeleaf.service.IRoleService;
import com.thymeleaf.service.ITokenService;
import com.thymeleaf.service.IUserService;
import com.thymeleaf.utils.Provider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AuthAPI {

    private final IUserService userService;

    private final JwtTokenProvider tokenProvider;

    private final ITokenService tokenService;

    private final IRoleService roleService;

    private final RestTemplate restTemplate;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login/oauth2/google")
    public ResponseEntity<?> handleGoogleLogin(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String accessToken = authorizationHeader.substring(7);

        final String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";

        Map<String, Object> userInfo = this.getUserInfo(accessToken, userInfoEndpoint);
        String email = (String) userInfo.get("email");

        UserDTO userDTO = userService.findByUserNameAndProviderId(email, Provider.google.name());
        if (userDTO == null) {
            userDTO = new UserDTO();
            userDTO.setUserName(email);
            userDTO.setProviderId(Provider.google.name());
        }
        if (userDTO.getPassword() == null) {
            userDTO.setPassword("123");
        }
        userDTO.setName((String) userInfo.get("name"));

        userDTO = userService.save(userDTO);
        // Generate JWT token and refresh token
        TokenDTO token = tokenProvider.generateToken(email,userDTO.getProviderId());
        token.setUserId(userDTO.getId());
        tokenService.save(token);
        List<String> roles = roleService.findByUser_Id(userDTO.getId()).stream().map(RoleDTO::getName).collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(token.getToken(), token.getRefreshToken(), userDTO.getUserName(), roles));

    }
    @PostMapping("/login/oauth2/github")
    public ResponseEntity<?> handleGithubLogin(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String accessToken = authorizationHeader.substring(7);

        final String userInfoEndpoint = "https://api.github.com/user";

        Map<String, Object> userInfo = this.getUserInfo(accessToken, userInfoEndpoint);
        String username = (String) userInfo.get("login");

        UserDTO userDTO = userService.findByUserNameAndProviderId(username, Provider.github.name());
        if (userDTO == null) {
            userDTO = new UserDTO();
            userDTO.setUserName(username);
            userDTO.setProviderId(Provider.github.name());
        }
        if (userDTO.getPassword() == null) {
            userDTO.setPassword("123");
        }
        userDTO.setName(username);
        userDTO = userService.save(userDTO);

        // Generate JWT token and refresh token
        TokenDTO token = tokenProvider.generateToken(username, userDTO.getProviderId());
        token.setUserId(userDTO.getId());
        tokenService.save(token);
        List<String> roles = roleService.findByUser_Id(userDTO.getId()).stream().map(RoleDTO::getName).collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(token.getToken(), token.getRefreshToken(), userDTO.getUserName(), roles));

    }
    @PostMapping("/login/oauth2/facebook")
    public ResponseEntity<?> handleFacebookLogin(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String accessToken = authorizationHeader.substring(7);

        final String userInfoEndpoint = "https://graph.facebook.com/me?fields=name,email,picture";

        Map<String, Object> userInfo = this.getUserInfo(accessToken, userInfoEndpoint);
        String username = (String) userInfo.get("email");

        UserDTO userDTO = userService.findByUserNameAndProviderId(username, Provider.facebook.name());
        if (userDTO == null) {
            userDTO = new UserDTO();
            userDTO.setUserName(username);
            userDTO.setProviderId(Provider.facebook.name());
        }
        if (userDTO.getPassword() == null) {
            userDTO.setPassword("123");
        }
        userDTO.setName((String) userInfo.get("name"));
        userDTO = userService.save(userDTO);

        // Generate JWT token and refresh token
        TokenDTO token = tokenProvider.generateToken(username, userDTO.getProviderId());
        token.setUserId(userDTO.getId());
        tokenService.save(token);
        List<String> roles = roleService.findByUser_Id(userDTO.getId()).stream().map(RoleDTO::getName).collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(token.getToken(), token.getRefreshToken(), userDTO.getUserName(), roles));

    }

    @PostMapping(value = {"/register"})
    public ResponseEntity<?> createUser(@RequestBody UserDTO model) {
        if (model.getUserName() == null || model.getPassword() == null || model.getUserName().isBlank() || model.getPassword().isBlank()) {
            String error = "Username and password is required";
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add(error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }
        if (checkUsername(model.getUserName())) {
            String error = "Username available";
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add(error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
        }
        model.setProviderId(Provider.local.name());
        model = userService.save(model);
        model.setPassword(null);
        return ResponseEntity.ok(model);
    }

    @PostMapping(value = "/signin")
    public ResponseEntity<?> login(@RequestBody LoginRequest user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        TokenDTO tokenDTO = tokenProvider.generateToken(customUserDetail.getUsername(), Provider.local.name());
        UserDTO userDTO = userService.findByUserName(user.getUserName(), 1);
        tokenDTO.setUserId(userDTO.getId());
        tokenService.save(tokenDTO);
        List<String> listRole = customUserDetail.getAuthorities().stream()
                .map(role -> role.getAuthority()).collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(tokenDTO.getToken(), tokenDTO.getRefreshToken(), customUserDetail.getUsername(), listRole));
    }

    private Map<String, Object> getUserInfo(String accessToken, String userInfoEndpoint) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                userInfoEndpoint, HttpMethod.GET, entity, Map.class);

        return response.getBody();
    }

    private boolean checkUsername(String username) {
        return userService.findByUserNameAndProviderId(username, Provider.local.name()) != null;
    }
}
