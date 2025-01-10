package com.project.animal.config;

import com.project.animal.util.JwtUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    // JwtUtil 주입
    public JwtHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // URL에서 토큰 추출
        String query = request.getURI().getQuery();
        if (query != null && query.contains("token=")) {
            String token = query.split("token=")[1].split("&")[0];

            // 토큰 검증
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getIdFromToken(token); // 토큰에서 사용자 ID 추출
                attributes.put("userId", userId); // WebSocket 세션에 저장
                return true;
            }
        }
        response.setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN); // 검증 실패 시 403
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 핸드셰이크 이후 로직 (필요시 구현)
    }
}
