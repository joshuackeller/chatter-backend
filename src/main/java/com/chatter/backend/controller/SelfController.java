package com.chatter.backend.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.chatter.backend.model.Account;
import com.chatter.backend.repository.AccountRepository;

@RestController
@RequestMapping(path = "/self")
public class SelfController {
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping()
    public @ResponseBody Account getAllChats(@RequestAttribute UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }

}
