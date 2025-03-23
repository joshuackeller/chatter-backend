package com.chatter.backend.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.chatter.backend.model.Account;
import com.chatter.backend.model.Chat;
import com.chatter.backend.model.ChatAccount;
import com.chatter.backend.model.ChatAccountId;
import com.chatter.backend.model.Message;
import com.chatter.backend.model.MessageKey;
import com.chatter.backend.repository.AccountRepository;
import com.chatter.backend.repository.ChatAccountRepository;
import com.chatter.backend.repository.ChatRepository;
import com.chatter.backend.repository.MessageRepository;
import com.chatter.backend.model.MessageWithAccountDTO;

@Service
public class ChatService {
    ChatRepository chatRepository;
    ChatAccountRepository chatAccountRepository;
    MessageService messageService;
    MessageRepository messageRepository;
    AccountRepository accountRepository;
    AccountService accountService;

    public ChatService(ChatRepository chatRepository, ChatAccountRepository chatAccountRepository,
            MessageService messageService, MessageRepository messageRepository, AccountRepository accountRepository,
            AccountService accountService) {
        this.chatRepository = chatRepository;
        this.chatAccountRepository = chatAccountRepository;
        this.messageService = messageService;
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    public void checkAccountExists(UUID chatId, UUID accountId) {
        if (!this.chatAccountRepository.existsById(new ChatAccountId(chatId, accountId))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat account not found");
        }
    }

    public List<Chat> findChatsByAccountId(UUID accountId) {

        List<Object[]> results = chatRepository.findChatsByAccountId(accountId);
        List<Chat> chats = new ArrayList<>();

        for (Object[] row : results) {
            Chat chat = new Chat();
            chat.setId((UUID) row[0]);
            chat.setName((String) row[1]);
            chat.setLastMessageContent((String) row[2]);

            // Convert Timestamp to LocalDateTime
            chat.setLastMessageAt(row[3] != null ? ((Timestamp) row[3]).toLocalDateTime() : null);
            chat.setCreatedAt(row[4] != null ? ((Timestamp) row[4]).toLocalDateTime() : null);
            chat.setLastReadAt(row[5] != null ? ((Timestamp) row[5]).toLocalDateTime() : null);

            chats.add(chat);
        }
        return chats;

    }

    public Chat createChat(String name, UUID accountId) {
        Chat chat = new Chat();
        chat.setName(name);
        chat.setCreatedAt(LocalDateTime.now());
        Chat newChat = chatRepository.save(chat);

        ChatAccount chatAccount = new ChatAccount();
        chatAccount.setId(new ChatAccountId(chat.getId(), accountId));
        chatAccount.setJoinedAt();
        chatAccountRepository.save(chatAccount);

        return newChat;
    }

    public Optional<Chat> getChat(UUID chatId, UUID accountId) {
        this.checkAccountExists(chatId, accountId);

        ChatAccount chatAccount = chatAccountRepository.findById(new ChatAccountId(chatId, accountId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat Account not found"));
        chatAccount.setLastReadAt();
        chatAccountRepository.save(chatAccount);

        return chatRepository.findById(chatId);
    }

    public List<MessageWithAccountDTO> getChatMessages(UUID chatId, UUID accountId) {
        this.checkAccountExists(chatId, accountId);

        return messageService.getMessagesWithAccount(chatId);
    }

    public Account addChatAccount(UUID chatId, UUID accountId, String username) {
        this.checkAccountExists(chatId, accountId);

        Account newAccount = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        ChatAccount chatAccount = new ChatAccount();

        chatAccount.setId(new ChatAccountId(chatId, newAccount.getId()));
        chatAccount.setJoinedAt();
        chatAccountRepository.save(chatAccount);

        return newAccount;

    }

    public MessageWithAccountDTO createChatMessage(UUID chatId, UUID accountId, UUID messageId, String content) {
        this.checkAccountExists(chatId, accountId);

        // Create new message
        Message message = new Message();
        MessageKey messageKey = new MessageKey();
        messageKey.setId(messageId);
        messageKey.setChatId(chatId);
        messageKey.setCreatedAt(new Date());
        message.setKey(messageKey);
        message.setAccountId(accountId);
        message.setContent(content);
        Message newMessage = messageRepository.save(message);

        // Update chat
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found"));
        chat.setLastMessageContent(newMessage.getContent());
        chat.setLastMessageAt(LocalDateTime.now());
        chatRepository.save(chat);

        // Update ChatAccount
        ChatAccount chatAccount = chatAccountRepository.findById(new ChatAccountId(chatId, accountId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat Account not found"));
        chatAccount.setLastReadAt();
        chatAccountRepository.save(chatAccount);

        // Create MessageWithAccountDTO
        Account account = accountService.getAccountById(accountId);
        MessageWithAccountDTO messageWithAccount = new MessageWithAccountDTO();
        messageWithAccount.setKey(newMessage.getKey());
        messageWithAccount.setAccount(account);
        messageWithAccount.setContent(newMessage.getContent());

        return messageWithAccount;
    }

}
