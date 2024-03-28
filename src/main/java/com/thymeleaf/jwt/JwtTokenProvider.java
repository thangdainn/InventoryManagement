package com.thymeleaf.jwt;

import com.thymeleaf.security.CustomUserDetail;
import com.thymeleaf.utils.JWTConstant;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
    private final Key secretKey = new SecretKeySpec(JWTConstant.JWT_SECRET.getBytes(), SignatureAlgorithm.HS256.getJcaName());
//    create jwt from info of User
    public String generateToken(CustomUserDetail customUserDetail){
        Date currentDate = new Date();
        Date dateExpired = new Date(currentDate.getTime() + JWTConstant.JWT_EXPIRATION);
//        create from username
        return Jwts.builder()
                .setSubject(customUserDetail.getUsername())
                .setIssuedAt(currentDate)
                .setExpiration(dateExpired)
//                .signWith(secretKey)
                .signWith(secretKey)
                .compact();
    }

//    get info user from jwt
    public String getUserNameFromJwt(String token){
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken){
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e){
            log.error("Invalid JWT Token");
        } catch (ExpiredJwtException e){
            log.error("Expired JWT Token");
        } catch (UnsupportedJwtException e){
            log.error("Unsupported JWT Token");
        } catch (IllegalArgumentException e){
            log.error("JWT claims String is empty");
        }
        return false;
    }
}
