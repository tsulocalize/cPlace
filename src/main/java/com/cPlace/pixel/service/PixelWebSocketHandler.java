package com.cPlace.pixel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PixelWebSocketHandler implements WebSocketHandler {

    private final PixelWebSocketManager webSocketManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        webSocketManager.addSession(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        webSocketManager.removeSession(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private ConcurrentWebSocketSessionDecorator decorate(WebSocketSession session) {
        // time limit : 3s
        // buffer size : 5mb
        return new ConcurrentWebSocketSessionDecorator(session, 3000, 5 * 1024 * 1024);
    }
}
