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
public class SecurityConfig {
    private final JwtTokenizer jwtTokenizer;

    //모든 유저 허용
    String[] allAllowPage = new String[] {
            "/", "/login", "/signup", //메인, 로그인, 회원가입
            "/js/**", "/css/**", "/images/**", "/static/**", //resources
            "/api/auth/login", "/api/auth/signup", //로그인, 회원가입 API 요청
            "/post/post-list", "/api/postList", "/api/postList/{postId}", "/postList/{postId}", //게시글 조회, 게시글 상세보기는 모두 가능
            "/review/{kakaoId}", "/api/reviewlist/{cafeId}" //리뷰도 모두 조회 가능
    };

    //관리자 페이지
    String[] adminAllowPage = new String[] {
            "/admin/**", "/api/admin/**"
    };

    //인증받은 회원만 접근 가능
    String[] authPage = new String[] {
            "/api/post/create", //글 작성은 인증된 회원만
            "/mypage/{userId}", "/api/profileImage/**", //마이페이지, 프로필 변경
            "/mymap","/mymap/update/{mapId}", //나만의 지도
    };


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
                        .requestMatchers(allAllowPage).permitAll()
                        .requestMatchers(adminAllowPage).hasAuthority("ADMIN") //접두사 제거
                        .requestMatchers(authPage).authenticated()
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
