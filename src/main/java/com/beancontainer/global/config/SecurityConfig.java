package com.beancontainer.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/", "/login", "/signup", "/js/**", "/css/**").permitAll() // 모든 사용자에게 허용
                        .requestMatchers("/post/create").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/mypage/{userId}").authenticated() // 인증된 사용자만 접근 가능
                        .requestMatchers("/admin").hasRole("ADMIN") // ROLE 이 ADMIN 인 사람만 접근 가능
                        .requestMatchers("/review/{kakaoId}").permitAll()
                        .anyRequest().authenticated() //그 외 모든 요청은 인증 필요
                )
                .formLogin(form -> form
                        .loginPage("/login") //loginPage 지정
                        .loginProcessingUrl("/login") //로그인 처리 URL
                        .defaultSuccessUrl("/", true) // 성공 시 / 로 리다이렉트
                        .usernameParameter("userId") //기본적으로 username 을 파라미터로 받음 -> 현재 userId를 받고 있으니 명시해줌
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
