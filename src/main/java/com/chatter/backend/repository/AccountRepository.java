package com.chatter.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.chatter.backend.model.Account;

public interface AccountRepository extends CrudRepository<Account, UUID> {
    Optional<Account> findByUsername(String username);

}
