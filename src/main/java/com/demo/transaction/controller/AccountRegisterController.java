package com.demo.transaction.controller;

import com.demo.transaction.dto.AccountRequestDto;
import com.demo.transaction.dto.AccountResponseDto;
import com.demo.transaction.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
public class AccountRegisterController {

    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponseDto register(@RequestBody AccountRequestDto request) {
        return accountService.register(request);
    }
}
