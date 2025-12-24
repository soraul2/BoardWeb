package com.wootae.BoardWeb.filter;

import com.wootae.BoardWeb.dto.CustomUserDetails;
import com.wootae.BoardWeb.entity.User;
import com.wootae.BoardWeb.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    //신분증을 내부 정보와 비교하여 일치하는지 확인하는 작업
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // [신분증 검색]
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null) {
            throw new AuthenticationServiceException("아이디 혹은 비밀번호를 확인해주세요");
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(authToken);
        //spring security가 customuserdetailsservice 에서 조회한 userdata 와 비교하여 success , fail 둘 중 하나를 선택해서 응다해준다.
    }

    //로그인 성공
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();

        Collection<? extends GrantedAuthority> collection = customUserDetails.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = collection.iterator();
        GrantedAuthority grantedAuthority = iterator.next();

        String role = grantedAuthority.getAuthority();
        String username = customUserDetails.getUsername();

        //token 을 만들어 줘서 cookie에 넣어준다.
        String token = jwtUtil.createJwt(username, role, 60 * 60 * 1000L);

        Cookie cookie = new Cookie("Authorization", token);

        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 1000);

        response.addCookie(cookie);

        response.sendRedirect("/main");
    }

    //로그인 실패
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        String errorMessage = "로그인 실패";
        if (failed instanceof AuthenticationServiceException) {
            errorMessage = failed.getMessage();
        } else {
            errorMessage = "아이디 또는 비밀번호를 확인해주세요.";
        }

        String encodedErrorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        response.sendRedirect("/login?error=true&exception=" + encodedErrorMessage);
    }

}
