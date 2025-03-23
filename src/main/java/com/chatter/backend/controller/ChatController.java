package com.chatter.backend.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.chatter.backend.model.Account;
import com.chatter.backend.model.Chat;
import com.chatter.backend.model.MessageWithAccountDTO;
import com.chatter.backend.service.ChatService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/chats")
public class ChatController {
    @Autowired
    private ChatService chatService;

    public ChatController() {
    }

    @GetMapping()
    public @ResponseBody List<Chat> getAllChats(@RequestAttribute UUID accountId) {
        return chatService.findChatsByAccountId(accountId);
    }

    private static class CreateChatRequest {
        @NotBlank(message = "Name is required")
        private String name;

        public String getName() {
            return name;
        }
    }

    @PostMapping()
    public @ResponseBody Chat createChat(@Valid @RequestBody CreateChatRequest createChatRequest,
            @RequestAttribute UUID accountId) {
        return chatService.createChat(createChatRequest.getName(), accountId);
    }

    @GetMapping("/{chatId}")
    public @ResponseBody ResponseEntity<Chat> getChatById(@PathVariable UUID chatId, @RequestAttribute UUID accountId) {
        Optional<Chat> chat = chatService.getChat(chatId, accountId);
        return chat.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{chatId}/accounts/{username}")
    public @ResponseBody Account addChatAccount(@PathVariable UUID chatId, @PathVariable String username,
            @RequestAttribute UUID accountId) {
        return chatService.addChatAccount(chatId, accountId, username);
    }

    @GetMapping("/{chatId}/messages")
    public @ResponseBody List<MessageWithAccountDTO> getChatMessages(@PathVariable UUID chatId,
            @RequestAttribute UUID accountId) {
        return chatService.getChatMessages(chatId, accountId);
    }

    private static class CreateChatMessageRequest {
        @NotNull(message = "Message ID cannot be empty")
        private UUID messageId;

        @NotBlank(message = "Content cannot be empty")
        private String content;

        public UUID getMessageId() {
            return messageId;
        }

        public String getContent() {
            return this.content;
        }
    }

    @PostMapping("/{chatId}/messages")
    public @ResponseBody MessageWithAccountDTO createChatMessage(
            @Valid @RequestBody CreateChatMessageRequest createChatMessageRequest, @PathVariable UUID chatId,
            @RequestAttribute UUID accountId) {
        return chatService.createChatMessage(chatId, accountId, createChatMessageRequest.getMessageId(),
                createChatMessageRequest.getContent());
    }

}
