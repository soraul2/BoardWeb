package com.wootae.BoardWeb.filter;

import com.wootae.BoardWeb.dto.CustomUserDetails;
import com.wootae.BoardWeb.entity.User;
import com.wootae.BoardWeb.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//[권한 체크] 어떤 권한이 필요한 페이지에 들어갔을 경우

//1.토큰이 쿠키에 존재하는 지
//2.토큰의 유효기간이 지나지 않았는지
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1. 필터를 타지 말아야 할 경로들 (화이트리스트)
        if (path.equals("/login") || path.equals("/join") || path.startsWith("/css/")) {
            filterChain.doFilter(request, response); // 검사 안 하고 다음 단계로 넘김
            return;
        }

        String token = null;

        //cookie 에서 Authorization 이 잘 있는지 , token의 유효기간이 유효한지 체크
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        for (Cookie cookie : cookies) {
            if ("Authorization".equals(cookie.getName())) {
                token = cookie.getValue();
            }
        }
        //토큰이 없거나 토큰이 만료 됐을 경우
        if (token == null || jwtUtil.isExpired(token)) {
            filterChain.doFilter(request, response);
            return;
        }


        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        User user = new User();
        user.setUsername(username);
        user.setRole(role);

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

}
