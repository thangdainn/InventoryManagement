package com.thymeleaf.jwt;

import com.thymeleaf.dto.TokenDTO;
import com.thymeleaf.security.CustomUserDetail;
import com.thymeleaf.utils.JWTConstant;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtTokenProvider {
    private final Key secretKey = new SecretKeySpec(JWTConstant.JWT_SECRET.getBytes(), SignatureAlgorithm.HS256.getJcaName());

    //    create jwt from info of User
    public TokenDTO generateToken(CustomUserDetail customUserDetail) {
        Date currentDate = new Date();
        Date dateExpired = new Date(currentDate.getTime() + JWTConstant.JWT_EXPIRATION);
//        create from username
        String token = Jwts.builder()
                .setSubject(customUserDetail.getUsername())
                .setIssuedAt(currentDate)
                .setExpiration(dateExpired)
                .signWith(secretKey)
                .compact();
        String refreshToken = generateRefreshToken();
        Date dateExpiredRefreshToken = new Date(currentDate.getTime() + JWTConstant.JWT_EXPIRATION_REFRESH);
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setToken(token);
        tokenDTO.setExpirationDate(new Timestamp(dateExpired.getTime()));
        tokenDTO.setRefreshToken(refreshToken);
        tokenDTO.setRefreshTokenExpirationDate(new Timestamp(dateExpiredRefreshToken.getTime()));
        return tokenDTO;
    }

    //    get info user from jwt
    public String getUserNameFromJwt(String token) {
        if (isTokenExpired(token)) {
            throw new ExpiredJwtException(null, null, "Token is expired");
        }
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(authToken);
        return true;
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    public boolean isTokenExpired(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody();
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}
