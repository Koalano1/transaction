package com.demo.transaction.service;

import com.demo.transaction.dto.AccountRequestDto;
import com.demo.transaction.dto.AccountResponseDto;
import com.demo.transaction.model.entities.Account;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AccountService {

//    ResponseEntity<Account> add(AccountRequestDto request);
//
//    List<Account> findByUserId(Long userId);

    ResponseEntity<Account> register(Account account);
}
