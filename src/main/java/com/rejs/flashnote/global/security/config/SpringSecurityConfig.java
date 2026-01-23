package com.rejs.flashnote.global.security.config;

import com.rejs.flashnote.global.security.service.CustomOidcService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {
    private final CustomOidcService oidcService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(Customizer.withDefaults())
                // 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("notes/**").authenticated()
                        .requestMatchers("decks/**").authenticated()
                        .anyRequest().permitAll()
                )
                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                )
                // OAuth2 로그인 설정
                .oauth2Login(
                        oauth2->oauth2
                                .loginPage("/login")
                                .userInfoEndpoint(userinfo->userinfo.oidcUserService(oidcService))
                );

        return http.build();
    }
}
