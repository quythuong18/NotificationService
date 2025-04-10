package com.qt.NotificationService.websocket;

import com.qt.NotificationService.security.JWTService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WSKeyAuthInterceptor implements HandshakeInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(WSKeyAuthInterceptor.class);
    private final JWTService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
        WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if(request instanceof ServletServerHttpRequest serverRequest) {
            String query = (String) serverRequest.getServletRequest().getQueryString();

            String username = query.split("username=")[1].split("&")[0];

            String key = serverRequest.getServletRequest().getHeader("token");
            LOGGER.warn(key);

            // using JWT to validate
            try {
                if(query.contains("username=") && jwtService.isValid(key, username)) {
                    attributes.put("authenticated", true);
                    attributes.put("username", username);
                    return true;
                }
            } catch (Exception e) {
                LOGGER.warn("User {} try to connect: {}", username, e.getMessage());

                // send error back to client
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                response.getHeaders().add("X-Error", e.getMessage());

            }
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
