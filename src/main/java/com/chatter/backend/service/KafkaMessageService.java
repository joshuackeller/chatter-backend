package com.chatter.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.chatter.backend.handler.WebSocketSessionManager;
import com.chatter.backend.model.MessageWithAccountDTO;

@Service
public class KafkaMessageService {

    @Value("${spring.kafka.topics.chat-messages:chat-messages}")
    private String chatMessagesTopic;

    @Autowired
    private KafkaTemplate<String, MessageWithAccountDTO> kafkaTemplate;

    @Autowired
    private WebSocketSessionManager sessionManager;

    public void sendMessage(MessageWithAccountDTO message) {
        String key = message.getKey().getChatId().toString();
        kafkaTemplate.send(chatMessagesTopic, key, message);
    }

    @KafkaListener(topics = "${spring.kafka.topics.chat-messages:chat-messages}")
    public void receiveMessage(MessageWithAccountDTO message) {
        try {
            sessionManager.broadcastToChat(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}