package com.chatter.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.chatter.backend.model.Message;
import com.chatter.backend.model.MessageKey;

@Repository
public interface MessageRepository extends CassandraRepository<Message, MessageKey> {

    @Query("SELECT * FROM message WHERE chat_id = ?0")
    List<Message> findByChatId(UUID chatId);
}
