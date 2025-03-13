package com.qt.NotificationService.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WSKeyAuthInterceptor implements HandshakeInterceptor {
    // this key would be fixed for all at first, I will generate one for each user per session later
    private static final String VALID_KEY = "klasdjfl_ajsd298389";
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if(request instanceof ServletServerHttpRequest serverRequest) {
            String key = serverRequest.getServletRequest().getHeader("key");
            if(VALID_KEY.equals(key)) {
                attributes.put("authenticated", true);
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
