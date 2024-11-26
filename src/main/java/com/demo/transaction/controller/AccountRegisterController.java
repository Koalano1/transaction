package com.demo.transaction.controller;

import com.demo.transaction.model.entities.Account;
import com.demo.transaction.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
public class AccountRegisterController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> register(@RequestBody Account account) {
        return accountService.register(account);
    }
}
