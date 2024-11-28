package com.demo.transaction.service.impl;

import com.demo.transaction.dto.AccountRequestDto;
import com.demo.transaction.dto.AccountResponseDto;
import com.demo.transaction.exception.UnprocessableEntityException;
import com.demo.transaction.mapper.AccountMapper;
import com.demo.transaction.model.entities.Account;
import com.demo.transaction.model.enums.AccountStatus;
import com.demo.transaction.repository.AccountRepository;
import com.demo.transaction.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    public AccountResponseDto register(AccountRequestDto request) {
        Optional<Account> existingUserWithUserName = accountRepository.findByUsername(request.getUsername());

        if (existingUserWithUserName.isPresent()) {
            throw new UnprocessableEntityException("User with username " + request.getUsername() + " already exists");
        }

        if (request.getStatus() == null) {
            request.setStatus(AccountStatus.ACTIVE);
        }

        if (request.getBalance() == null) {
            request.setBalance(BigDecimal.ZERO);
        }

        Account user = accountMapper.toEntity(request);
        accountRepository.save(user);

        return accountMapper.toDto(user);
    }

}
