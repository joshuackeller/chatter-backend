package com.chatter.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "chat")
public class Chat {
    @Id
    @GeneratedValue()
    @UuidGenerator()
    private UUID id;

    private String name;

    private String last_message_content;

    private LocalDateTime last_message_at;

    private LocalDateTime created_at;

    @Transient
    private LocalDateTime last_read_at;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public String getLastMessageContent() {
        return last_message_content;
    }

    public void setLastMessageContent(String last_message_content) {
        this.last_message_content = last_message_content;
    }

    public LocalDateTime getLastMessageAt() {
        return last_message_at;
    }

    public void setLastMessageAt(LocalDateTime last_message_at) {
        this.last_message_at = last_message_at;
    }

    public LocalDateTime getLastReadAt() {
        return last_read_at;
    }

    public void setLastReadAt(LocalDateTime last_read_at) {
        this.last_read_at = last_read_at;
    }
}
