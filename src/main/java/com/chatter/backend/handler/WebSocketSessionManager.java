package com.chatter.backend.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.chatter.backend.model.MessageWithAccountDTO;
import com.chatter.backend.repository.ChatAccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class WebSocketSessionManager {
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatAccountRepository chatAccountRepository;

    public void addSession(WebSocketSession session) {
        sessions.add(session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }

    public void broadcastToChat(MessageWithAccountDTO messageDTO) throws IOException {
        Set<UUID> accountIdsInChat = chatAccountRepository.findAccountIdsByChatId(messageDTO.getKey().getChatId());
        String jsonMessage = objectMapper.writeValueAsString(messageDTO);
        TextMessage textMessage = new TextMessage(jsonMessage);

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                UUID accountId = (UUID) session.getAttributes().get("accountId");
                if (accountId != null && accountIdsInChat.contains(accountId)) {
                    session.sendMessage(textMessage);
                }
            }
        }
    }
}