package com.chatter.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.chatter.backend.model.Chat;

public interface ChatRepository extends CrudRepository<Chat, UUID> {
    @Query(value = """
            SELECT c.id, c.name, c.last_message_content, c.last_message_at, c.created_at,
                   ca.last_read_at AS chat_last_read_at
            FROM chat c
            LEFT JOIN chat_account ca ON ca.chat_id = c.id
            WHERE ca.account_id = :account_id
            ORDER BY COALESCE(last_message_at, created_at) DESC, created_at DESC
            """, nativeQuery = true)
    List<Object[]> findChatsByAccountId(@Param("account_id") UUID accountId);

}
