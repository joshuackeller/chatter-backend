package com.chatter.backend.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.chatter.backend.model.ChatAccount;
import com.chatter.backend.model.ChatAccountId;

public interface ChatAccountRepository extends CrudRepository<ChatAccount, ChatAccountId> {
    @Query(value = "SELECT ca.account_id FROM chat_account ca WHERE ca.chat_Id = :chatId", nativeQuery = true)
    Set<UUID> findAccountIdsByChatId(@Param("chatId") UUID chatId);
}
