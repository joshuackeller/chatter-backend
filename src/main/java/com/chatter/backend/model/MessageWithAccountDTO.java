package com.chatter.backend.model;

import java.util.UUID;

public class MessageWithAccountDTO {

    private MessageKey key;
    private String content;
    private UUID account_id;
    private Account account;

    public MessageKey getKey() {
        return key;
    }

    public void setKey(MessageKey key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getAccountId() {
        return account_id;
    }

    public void setAccountId(UUID account_id) {
        this.account_id = account_id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}
