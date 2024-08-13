package com.beancontainer.global.config;

import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.global.jwt.filter.JwtAuthenticationFilter;
import com.beancontainer.global.jwt.util.JwtTokenizer;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
//@EnableGlobalAuthentication(prePostEnabled = true)
public class SecurityConfig {


    private final JwtTokenizer jwtTokenizer;
    private final MemberRepository memberRepository;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                 //세션 설정 -> 세션정보를 서버에 저장하지 않도록 함
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //경로별 인가 작업
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/signup", "/js/**", "/css/**", "/images/**", "/static/**").permitAll() // 모든 사용자에게 허용
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/signup").permitAll()
                        .requestMatchers("/post/post-list", "/api/post/post-list").permitAll()
                        .requestMatchers("/api/post/create").authenticated()
                        .requestMatchers("/mypage/{userId}", "/api/profileImage/**", "/mymap","/mymap/update/{mapId}").authenticated() // 인증된 사용자만 접근 가능
                        .requestMatchers("/admin/**", "/api/admin/**").hasAuthority("ADMIN") // ROLE 이 ADMIN 인 사람만 접근 가능 ROLE 접두사 제거
                        .requestMatchers("/review/{kakaoId}", "/api/reviewlist/{cafeId}").permitAll()
                        .requestMatchers("/chat/**").permitAll()
                        .anyRequest().authenticated() //그 외 모든 요청은 인증 필요
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .deleteCookies("accessToken", "refreshToken")
                        .permitAll()
                );
        //jwt 설정
        http
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenizer), UsernamePasswordAuthenticationFilter.class);

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
