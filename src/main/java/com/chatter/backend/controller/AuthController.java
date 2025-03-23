package com.chatter.backend.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatter.backend.exception.BadRequestException;
import com.chatter.backend.model.Account;
import com.chatter.backend.repository.AccountRepository;
import com.chatter.backend.utils.JwtUtil;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private static class LoginRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest loginRequest) {
        Account account = accountRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(),
                account.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        account.eraseCredentials();

        return jwtUtil.generateToken(account);
    }

    private static class RegisterRequest {
        @NotBlank(message = "Username is required")
        @Email(message = "Username must be a valid email")
        private String username;

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 charactersr long")
        private String password;

        @NotBlank(message = "Name is required")
        private String name;

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getName() {
            return name;
        }
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest registerRequest) {
        Account account = new Account();
        account.setUsername(registerRequest.getUsername());
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        account.setName(registerRequest.getName());
        account.setCreatedAt(LocalDateTime.now());
        account.setEnabled(true);

        Account savedAccount = accountRepository.save(account);

        account.eraseCredentials();

        return jwtUtil.generateToken(savedAccount);
    }

}
