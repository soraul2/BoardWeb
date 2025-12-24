package com.wootae.BoardWeb.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {

    private SecretKey secretKey;

    // 생성자: application.properties에서 secret 키를 가져와서 암호화 키 객체로 만듦
    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        
        // JJWT 0.12.x 버전부터는 SecretKey 객체를 명확하게 만들어야 합니다.
        // 알고리즘은 HS256을 사용합니다.
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), 
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // 1. 토큰에서 username(아이디) 꺼내기
    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get("username", String.class);
    }

    // 2. 토큰에서 role(권한) 꺼내기
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get("role", String.class);
    }

    // 3. 토큰이 만료되었는지 확인 (true: 만료됨, false: 유효함)
    public Boolean isExpired(String token) {
        // expiration 날짜가 현재 시간(new Date)보다 이전인지 확인
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    // 4. ★ 토큰 생성 (로그인 성공 시 호출됨)
    public String createJwt(String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("username", username) // Payload에 username 저장
                .claim("role", role)         // Payload에 role 저장
                .issuedAt(new Date(System.currentTimeMillis())) // 발행 시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료 시간
                .signWith(secretKey) // 서명 (암호화 알고리즘 + 시크릿 키)
                .compact();
    }
}