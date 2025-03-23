package com.chatter.backend.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class ChatAccountId implements Serializable {

    private UUID chat_id;
    private UUID account_id;

    // No-argument constructor (required by Hibernate)
    public ChatAccountId() {
    }

    public ChatAccountId(UUID chat_id, UUID account_id) {
        this.chat_id = chat_id;
        this.account_id = account_id;
    }

    public UUID getChatId() {
        return chat_id;
    }

    public void setChatId(UUID chat_id) {
        this.chat_id = chat_id;
    }

    public UUID getAccountId() {
        return account_id;
    }

    public void setAccountId(UUID account_id) {
        this.account_id = account_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ChatAccountId that = (ChatAccountId) o;

        if (!chat_id.equals(that.chat_id))
            return false;
        return account_id.equals(that.account_id);
    }

    @Override
    public int hashCode() {
        int result = chat_id.hashCode();
        result = 31 * result + account_id.hashCode();
        return result;
    }

}