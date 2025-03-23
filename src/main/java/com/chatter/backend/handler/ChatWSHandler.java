package com.chatter.backend.handler;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.chatter.backend.model.MessageWithAccountDTO;
import com.chatter.backend.service.ChatService;
import com.chatter.backend.service.KafkaMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

public class ChatWSHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final KafkaMessageService kafkaMessageService;
    private final WebSocketSessionManager sessionManager;

    public ChatWSHandler(ObjectMapper objectMapper, ChatService chatService,
            KafkaMessageService kafkaMessageService,
            WebSocketSessionManager sessionManager) {
        this.objectMapper = objectMapper;
        this.chatService = chatService;
        this.kafkaMessageService = kafkaMessageService;
        this.sessionManager = sessionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UUID accountId = (UUID) session.getAttributes().get("accountId");

        if (accountId != null) {
            sessionManager.addSession(session);
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Unauthorized: No accountId found"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionManager.removeSession(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        MessageWithAccountDTO messageDTO = objectMapper.readValue(payload, MessageWithAccountDTO.class);
        UUID accountId = (UUID) session.getAttributes().get("accountId");

        if (!accountId.equals(messageDTO.getAccountId())) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Unauthorized: Account ID mismatch"));
            return;
        }

        chatService.checkAccountExists(messageDTO.getKey().getChatId(), messageDTO.getAccountId());

        kafkaMessageService.sendMessage(messageDTO);
    }
}