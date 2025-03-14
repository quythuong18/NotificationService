package com.qt.NotificationService.websocket;

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
public class WSKeyAuthInterceptor implements HandshakeInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(WSKeyAuthInterceptor.class);

    // this key would be fixed for all at first, I will generate one for each user per session later
    private static final String VALID_KEY = "klasdjfl_ajsd298389";
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if(request instanceof ServletServerHttpRequest serverRequest) {
            String query = (String) serverRequest.getServletRequest().getQueryString();

//            String username = (String) serverRequest.getServletRequest().getSession()
//                    .getAttribute("username");

            String key = serverRequest.getServletRequest().getHeader("key");

            if(query != null && query.contains("username=") && VALID_KEY.equals(key)) {
                String username = query.split("username=")[1].split("&")[0];
                attributes.put("authenticated", true);
                attributes.put("username", username);
                LOGGER.info("Valid sent key before WS hand shake");
                return true;
            }
        }
        LOGGER.warn("Invalid sent key before WS hand shake");
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
