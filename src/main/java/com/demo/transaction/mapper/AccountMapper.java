package com.demo.transaction.mapper;

import com.demo.transaction.dto.AccountRequestDto;
import com.demo.transaction.dto.AccountResponseDto;
import com.demo.transaction.model.entities.Account;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AccountMapper {
    public Account toEntity(AccountRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Account account = new Account();
        account.setUsername(dto.getUsername());
        account.setEmail(dto.getEmail());
        account.setBalance(dto.getBalance());
        account.setStatus(dto.getStatus());
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        return account;
    }

    public AccountResponseDto toDto(Account entity) {
        if (entity == null) {
            return null;
        }

        AccountResponseDto accountResponseDto = new AccountResponseDto();
        accountResponseDto.setUsername(entity.getUsername());
        accountResponseDto.setEmail(entity.getEmail());
        accountResponseDto.setBalance(entity.getBalance());
        accountResponseDto.setStatus(entity.getStatus());
        accountResponseDto.setCreatedAt(entity.getCreatedAt());
        accountResponseDto.setUpdatedAt(entity.getUpdatedAt());
        return accountResponseDto;
    }

}
