package com.rejs.flashnote.global.security.config;

import com.rejs.flashnote.global.security.benchmark.BenchmarkAuthenticationFilter;
import com.rejs.flashnote.global.security.service.CustomOidcService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {
    private final CustomOidcService oidcService;
    private final Optional<BenchmarkAuthenticationFilter> benchmarkAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if(benchmarkAuthenticationFilter.isPresent()){
            http.csrf(AbstractHttpConfigurer::disable);
        }else {
            http.csrf(Customizer.withDefaults());
        }
        http
                // 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("notes/**").authenticated()
                        .requestMatchers("decks/**").authenticated()
                        .requestMatchers("cards/**").authenticated()
                        .requestMatchers("/api/study/**").authenticated()
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

        benchmarkAuthenticationFilter.ifPresent((filter)->{
            http.addFilterBefore(filter, AnonymousAuthenticationFilter.class);
        });

        return http.build();
    }
}
