package com.cPlace.pixel.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Log4j2
public class PixelWebSocketManager {

    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static final PingMessage PING_MESSAGE = new PingMessage();

    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
        log.info("connected with %s".formatted(session.getId()));
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session.getId());
        log.info("disconnected with %s".formatted(session.getId()));
    }

    public synchronized void sendPixelChangeToAll(BinaryMessage message) {
        sessions.forEach((id, session) -> {
            sendPixels(message, session);
        });
    }

    @Async
    public void sendPixels(BinaryMessage message, WebSocketSession session) {
        if (!session.isOpen()) return;
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            log.error("메시지 전송에 실패했습니다");
            // retry 해보고 close 하기
            try {
                session.close();
            } catch (IOException ex) {
                throw new RuntimeException("세션 종료에 실패했습니다. :" + ex);
            }
        }
    }

    @Scheduled(fixedRateString = "#{${websocket.ping-interval}}")
    void sendPingToAll() {
        sessions.values()
                .forEach(session -> {
                    try {
                        session.sendMessage(PING_MESSAGE);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
