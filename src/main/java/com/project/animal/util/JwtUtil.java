package com.project.animal.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "RA4A7xk3zhWx3czBZPCtZpbalul7W4uZ1p3NzTjAC9kFbHX3CmRtL7";
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간
    private final long REFRESH_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 7; // 7일

    // 액세스 토큰 생성
    public String generateToken(Long id, String email) {
        return Jwts.builder()
                .setSubject(String.valueOf(id)) // ID를 Subject에 저장
                .claim("email", email)         // 이메일은 클레임으로 저장
                .setIssuedAt(new Date())       // 토큰 생성 시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 서명
                .compact();
    }

    // 리프레시 토큰 생성
    public String generateRefreshToken(Long id) {
        return Jwts.builder()
                .setSubject(String.valueOf(id)) // ID를 Subject에 저장
                .setIssuedAt(new Date())       // 토큰 생성 시간
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME)) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 서명
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid token: " + e.getMessage());
            return false;
        }
    }

    // ID 추출 (Subject에 저장된 ID 반환)
    public Long getIdFromToken(String token) {
        String subject = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return Long.valueOf(subject); // Subject를 Long 타입으로 변환
    }

    // 이메일 추출
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class); // 클레임에서 이메일 추출
    }
}
