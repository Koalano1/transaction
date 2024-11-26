package com.demo.transaction.service.impl;

import com.demo.transaction.exception.AccountProcessingException;
import com.demo.transaction.mapper.AccountMapper;
import com.demo.transaction.model.entities.Account;
import com.demo.transaction.repository.AccountRepository;
import com.demo.transaction.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public ResponseEntity<Account> register(Account account) {
        Optional<Account> existingUserWithUserName = accountRepository.findByUsername(account.getUsername());

        if (existingUserWithUserName.isPresent()) {
            throw new AccountProcessingException("User with username " + account.getUsername() + " already exists");
        }

        Account user = Account.builder()
                .username(account.getUsername())
                .email(account.getEmail())
                .balance(BigDecimal.ZERO)
                .build();

        accountRepository.save(user);

        return ResponseEntity.ok(user);
    }

}
