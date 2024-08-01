package com.beancontainer.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                        .requestMatchers("/","/loginform", "/signupform", "/post/create", "api/post/create").permitAll() // 모든 사용자에게 허용
                        .requestMatchers("/mypage/{userId}").authenticated() // 인증된 사용자만 접근 가능
                        .requestMatchers("/admin").hasRole("ADMIN") // ROLE 이 ADMIN 인 사람만 접근 가능
                        .anyRequest().authenticated() //그 외 모든 요청은 인증 필요
                        )
                        .formLogin(form -> form
                                .loginPage("/loginform") //loginPage 지정
                                .defaultSuccessUrl("/") // 성공 시 / 로 리다이렉트
                                .permitAll()
                        )
                        .logout(logout -> logout
                                .logoutSuccessUrl("/")
                                .permitAll()
                        );
                return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
