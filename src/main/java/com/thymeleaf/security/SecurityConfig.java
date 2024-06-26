package com.thymeleaf.security;

import com.thymeleaf.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

//    @Autowired
//    private IUserRepository userRepository;

//    @Autowired
//    private CustomSuccessHandler customSuccessHandler;
//
//    @Autowired
//    private CustomOAuth2UserService customOAuth2UserService;

//    @Bean
//    public OidcUserService oidcUserService() {
//        return new CustomOidcUserService(userRepository);
//    }

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;


    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter(exceptionResolver);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
//    @Bean
//    public JwtDecoder jwtDecoder() {
//        return NimbusJwtDecoder.withJwkSetUri("https://www.googleapis.com/oauth2/v3/certs").build();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Autowired
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailService();
    }

    @Autowired
    public LogoutHandlerService logoutHandlerService;

    @Bean
    @Order(1)
    public SecurityFilterChain authSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable())
                .securityMatcher(new OrRequestMatcher(new AntPathRequestMatcher("/login/oauth2/**"),
                        new AntPathRequestMatcher("/register"),
                        new AntPathRequestMatcher("/signin")
                ))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/login/oauth2/**","/register", "/signin")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//                .authenticationProvider(authenticationProvider());
//                .oauth2ResourceServer(oauth2 -> oauth2
//                        .jwt(jwt -> jwt.decoder(jwtDecoder())
//                        )
//                );

//
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain refreshTokenSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable())
                .securityMatcher(new AntPathRequestMatcher("/refresh-token"))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/refresh-token")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable())
                .securityMatcher(new AntPathRequestMatcher("/api/**"))
                .authorizeHttpRequests((requests) -> requests
//                        .requestMatchers("/vendors/**", "/build/**", "/css/**", "/images/**", "/js/**")
//                        .permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//                .oauth2Login( oauth2 -> oauth2
//                                .loginPage("/login")
//                                .permitAll()
//                                .defaultSuccessUrl("/auth", true)
//                                .failureUrl("/login?abc")
//                                .userInfoEndpoint(
//                                        userInfo -> userInfo
//                                                .userService(customOAuth2UserService)
//                                                .oidcUserService(oidcUserService())
//                                )
//
//                )
//                .oauth2ResourceServer(oauth2 -> oauth2
//                        .jwt(jwt -> jwt.decoder(jwtDecoder())
//                        )
//                );
//                .oauth2Login(Customizer.withDefaults()
//                );
//                .formLogin((form) -> form
//                        .loginPage("/login")
//                        .usernameParameter("userName")
//                        .passwordParameter("password")
//                        .permitAll()
//                        .successHandler(customSuccessHandler)
//                        .failureUrl("/loginFail")
////                        .failureForwardUrl("/loginFail")
////                        .loginProcessingUrl("/login")
//                );
//                .logout((logout) -> logout
//                        .logoutSuccessUrl("/login")
//                        .invalidateHttpSession(true));

//        http.authenticationProvider(authenticationProvider());
//        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    @Order(4)
    public SecurityFilterChain logoutSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable())
                .securityMatcher(new AntPathRequestMatcher("/logout"))
                .authorizeHttpRequests((requests) -> requests
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(logoutHandlerService)
                        .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext()))
                );

        return http.build();
    }



    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public ProviderManager authManagerBean(HttpSecurity security) throws Exception {
        return (ProviderManager) security.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider()).build();
    }
}
