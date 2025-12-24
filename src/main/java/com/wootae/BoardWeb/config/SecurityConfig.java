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
                .requestMatchers("/login", "/join","/main").permitAll()
                .requestMatchers("/user/**").hasAnyRole("USER")

        );
        http.addFilterBefore(new JwtFilter(jwtUtil),UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(new LoginFilter(jwtUtil,authenticationManager), UsernamePasswordAuthenticationFilter.class);
        http.logout((logout)->logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("Authorization")
        );
        return http.build();
    }


}
