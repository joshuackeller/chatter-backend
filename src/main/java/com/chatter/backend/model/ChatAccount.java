package com.chatter.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_account")
public class ChatAccount {
    @EmbeddedId
    private ChatAccountId id;

    private LocalDateTime joined_at;

    private LocalDateTime last_read_at;

    public ChatAccountId getId() {
        return id;
    }

    public void setId(ChatAccountId id) {
        this.id = id;
    }

    public LocalDateTime getJoinedAt() {
        return joined_at;
    }

    public void setJoinedAt() {
        this.joined_at = LocalDateTime.now();
    }

    public LocalDateTime getLastReadAt() {
        return last_read_at;
    }

    public void setLastReadAt() {
        this.last_read_at = LocalDateTime.now();
    }
}
