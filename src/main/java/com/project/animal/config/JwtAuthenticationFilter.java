package com.project.animal.config;

import com.project.animal.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Authorization 헤더가 없거나 형식이 올바르지 않은 경우
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // "Bearer " 이후 토큰 부분만 가져옴

        try {
            // 토큰 검증 및 사용자 ID 추출
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getIdFromToken(token); // ID 추출
                System.out.println("Extracted ID: " + userId); // 디버깅용 로그

                // UserDetails 객체 생성
                UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(userId));

                // principal에 사용자 ID를 포함한 Authentication 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {

            // 토큰이 유효하지 않거나 검증 실패 시 처리
            System.err.println("JWT 검증 실패: " + e.getMessage());
            SecurityContextHolder.clearContext(); // 보안 컨텍스트 초기화
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
            return;
        }

        chain.doFilter(request, response); // 다음 필터로 진행
    }

}



