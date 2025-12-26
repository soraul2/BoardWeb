package com.wootae.BoardWeb.config;

import com.wootae.BoardWeb.filter.JwtFilter;
import com.wootae.BoardWeb.filter.LoginFilter;
import com.wootae.BoardWeb.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;

    //비밀번호 단방향 암호화 할 때 필요
    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {

        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        //csrf , HttpBasic , formLogin , session (JWT 방식)
        http.csrf((auth) -> auth.disable());
        http.formLogin((auth) -> auth.disable());
        http.httpBasic((auth) -> auth.disable());
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests((auth) -> auth
                // 1. [완전 허용] 누구나 들어올 수 있는 곳 (순서가 제일 중요합니다! ⭐)
                // 게시판 리스트, 상세 보기(detail/숫자)는 허용
                .requestMatchers("/", "/login", "/join", "/main", "/board/list", "/board/detail/**").permitAll()

                // 2. [인증 필요] 위에서 허용된 것들을 *제외한* 나머지 /board 하위 경로는 전부 로그인 필요
                // (write, edit, update, delete, like 등등... 일일이 안 적어도 됨!)
                .requestMatchers("/board/**", "/user/**").hasAnyRole("USER")

                // 3. [나머지] 그 외 모든 페이지는 로그인해야 접근 가능
                .anyRequest().authenticated()
        );
        http.addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(new LoginFilter(jwtUtil, authenticationManager), UsernamePasswordAuthenticationFilter.class);
        http.logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("Authorization")
        );
        return http.build();
    }


}
