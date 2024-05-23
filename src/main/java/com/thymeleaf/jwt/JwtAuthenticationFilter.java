package com.thymeleaf.jwt;

import com.thymeleaf.security.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private HandlerExceptionResolver exceptionResolver;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    public JwtAuthenticationFilter(HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthenticationFilter is called");

        try {
            if (isByPassToken(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                String userName = jwtTokenProvider.getUserNameFromJwt(jwt);
                String providerId = jwtTokenProvider.getProviderIdFromJwt(jwt);
//                UserDetails userDetails = customUserDetailService.loadUserByUsername(userName);
                UserDetails userDetails = customUserDetailService.loadUserByUsernameAndProviderId(userName, providerId);
                if (userDetails != null) {
//                    Set thong tin cho security context
                    UsernamePasswordAuthenticationToken authenticationToken
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

            }
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            exceptionResolver.resolveException(request, response, null, e);
        }
    }

    private boolean isByPassToken(@NonNull HttpServletRequest request) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        final List<Pair<String, String>> byPassToken = Arrays.asList(
                new Pair<>("/register", "POST"),
                new Pair<>("/vendors/**", "GET"),
                new Pair<>("/build/**", "GET"),
                new Pair<>("/css/**", "GET"),
                new Pair<>("/images/**", "GET"),
                new Pair<>("/js/**", "GET"),
                new Pair<>("/refresh-token", "POST"),
                new Pair<>("/signin", "POST"),
                new Pair<>("/login/**", "POST")
        );
        String uri = request.getRequestURI();
        String method = request.getMethod();
        for (Pair<String, String> pair : byPassToken) {
            if (antPathMatcher.match(pair.a, uri) && antPathMatcher.match(pair.b, method)) {
                return true;
            }
        }
        return false;
    }
}
