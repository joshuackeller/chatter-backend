package com.chatter.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.chatter.backend.model.Account;
import com.chatter.backend.repository.AccountRepository;
import com.chatter.backend.utils.LRUCache;

class Node {
    UUID id;
    Account Account;
    Node prev;
    Node next;
}

@Service
public class AccountService {

    AccountRepository accountRepository;
    LRUCache<UUID, Account> cache;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.cache = new LRUCache<>(10000);
    }

    public Account getAccountById(UUID id) {
        Account account = cache.get(id);
        if (account == null) {
            account = accountRepository.findById(id).orElse(null);
            if (account == null) {
                return null;
            }
            cache.put(account.getId(), account);
        }
        return account;
    }

    public List<Account> getAccountsById(List<UUID> ids) {

        List<UUID> idsToFetch = ids.stream().filter(id -> !cache.containsKey(id)).collect(Collectors.toList());

        Iterable<Account> newAccounts = accountRepository.findAllById(idsToFetch);

        for (Account account : newAccounts) {
            cache.put(account.getId(), account);
        }

        List<Account> result = new ArrayList<>();
        for (UUID id : ids) {
            Account account = cache.get(id);
            if (account != null) {
                result.add(account);
            }
        }

        return result;

    }

}
