package com.beancontainer.global.config;

import com.beancontainer.global.jwt.filter.JwtAuthenticationFilter;
import com.beancontainer.global.jwt.util.JwtTokenizer;
import com.beancontainer.global.oauth2.handler.CustomSuccessHandler;
import com.beancontainer.global.oauth2.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtTokenizer jwtTokenizer;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;

    public SecurityConfig(JwtTokenizer jwtTokenizer, CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler) {
        this.jwtTokenizer = jwtTokenizer;
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
    }

    //모든 유저 허용
    String[] allAllowPage = new String[] {
            "/", "/login", "/signup", //메인, 로그인, 회원가입
            "/js/**", "/css/**", "/images/**", "/static/**", //resources
            "/api/auth/login", "/api/auth/signup/**", "/api/auth/check-userid", "/api/auth/logout", //로그인, 로그아웃, 회원가입 API 요청
            "/post/post-list", "/api/postList", "/api/postList/{postId}", "/postList/{postId}", //게시글 조회, 게시글 상세보기는 모두 가능
            "/api/postlist/comments/{postId}",  // 댓글 목록
            "/review/{kakaoId}", "/api/reviewlist/{cafeId}", "/reviewlist/{cafeId}", //리뷰도 모두 조회 가능
            "mymap/{mapId}",
            "api/mymap/{mapId}",
            "/api/cafe/{cafeId}", // 카페정보
            "/api/map/category", //카테고리 저장 후 검색
            "/api/randommap",
            "/api/review/create"

    };

    //관리자 페이지
    String[] adminAllowPage = new String[] {
            "/admin/**", "/api/admin/**"
    };

    //인증받은 회원만 접근 가능
    String[] authPage = new String[] {
            "/api/post/create", //글 작성은 인증된 회원만
            "/mypage/{userId}", "/api/profileImage/**", "/api/mypage/{userId}/deleteProfileImage", //마이페이지, 프로필 변경
            "/mymap","/mymap/update/{mapId}",
            "/api/mymap/delete/{mapId}", "/api/mymap/update/{mapId}", "/api/mymap", //나만의 지도
            "/api/review/delete/{reviewId}", "/api/review/update/{reviewId}", //리뷰 작성, 수정, 삭제
            "/chat/**" //모든 채팅
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                 //세션 설정 -> 세션정보를 서버에 저장하지 않도록 함
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(allAllowPage).permitAll()
                    .requestMatchers(adminAllowPage).hasAuthority("ADMIN") //접두사 제거
                    .requestMatchers(authPage).authenticated()
                    .anyRequest().authenticated() //그 외 모든 요청은 인증 필요
        )
                //oauth2 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService))
                        .authorizationEndpoint(authorization -> authorization.baseUri("/oauth2/authorization"))
                        .successHandler(customSuccessHandler))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenizer), UsernamePasswordAuthenticationFilter.class);
        http
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .deleteCookies("accessToken", "refreshToken")
                        .permitAll()
                );


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
