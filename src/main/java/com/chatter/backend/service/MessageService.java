package com.chatter.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.chatter.backend.model.Account;
import com.chatter.backend.model.Message;
import com.chatter.backend.model.MessageWithAccountDTO;
import com.chatter.backend.repository.MessageRepository;

@Service
public class MessageService {
    MessageRepository messageRepository;
    AccountService accountService;

    public MessageService(MessageRepository messageRepository, AccountService accountService) {
        this.messageRepository = messageRepository;
        this.accountService = accountService;
    }

    public List<MessageWithAccountDTO> getMessagesWithAccount(UUID chatId) {
        List<Message> messages = messageRepository.findByChatId(chatId);
        List<MessageWithAccountDTO> result = new ArrayList<>();

        List<UUID> accountIds = messages.stream().map(Message::getAccountId).distinct().collect(Collectors.toList());

        List<Account> accounts = accountService.getAccountsById(accountIds);

        Map<UUID, Account> accountMap = accounts.stream().collect(Collectors.toMap(Account::getId, account -> account));

        for (Message message : messages) {
            MessageWithAccountDTO dto = new MessageWithAccountDTO();
            dto.setKey(message.getKey());
            dto.setContent(message.getContent());
            dto.setAccountId(message.getAccountId());

            Account account = accountMap.get(message.getAccountId());
            dto.setAccount(account);

            result.add(dto);
        }

        return result;
    }
}
