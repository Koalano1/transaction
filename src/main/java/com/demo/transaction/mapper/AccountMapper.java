package com.demo.transaction.mapper;

import com.demo.transaction.dto.AccountRequestDto;
import com.demo.transaction.dto.AccountResponseDto;
import com.demo.transaction.model.entities.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    public static Account toEntity(AccountRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Account account = new Account();
        account.setUsername(dto.getUsername());
        account.setEmail(dto.getEmail());
        account.setBalance(dto.getBalance());
        account.setStatus(dto.getStatus());
        return account;
    }

    public static AccountRequestDto toDto(Account entity) {
        if (entity == null) {
            return null;
        }

        AccountRequestDto accountRequestDto = new AccountRequestDto();
        accountRequestDto.setUsername(entity.getUsername());
        accountRequestDto.setEmail(entity.getEmail());
        accountRequestDto.setBalance(entity.getBalance());
        accountRequestDto.setStatus(entity.getStatus());
        return accountRequestDto;
    }

}
