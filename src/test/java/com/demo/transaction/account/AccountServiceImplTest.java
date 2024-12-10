package com.demo.transaction.account;

import com.demo.transaction.dto.AccountRequestDto;
import com.demo.transaction.dto.AccountResponseDto;
import com.demo.transaction.exception.UnprocessableEntityException;
import com.demo.transaction.mapper.AccountMapper;
import com.demo.transaction.model.entities.Account;
import com.demo.transaction.model.enums.AccountStatus;
import com.demo.transaction.repository.AccountRepository;
import com.demo.transaction.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private AccountRequestDto accountRequestDto;
    private Account account;
    private AccountResponseDto accountResponseDto;

    @BeforeEach
    void setUp() {
        accountRequestDto = new AccountRequestDto();
        accountRequestDto.setUsername("user123");
        accountRequestDto.setBalance(new BigDecimal("1000.00"));

        accountResponseDto = new AccountResponseDto();
        accountResponseDto.setUsername("user123");
        accountResponseDto.setBalance(new BigDecimal("1000.00"));
        accountResponseDto.setStatus(AccountStatus.ACTIVE);

        account = new Account();
        account.setUsername("user123");
        account.setBalance(new BigDecimal("1000.00"));
        account.setStatus(AccountStatus.ACTIVE);
    }

    @Test
    void register_userAlreadyExists_throwsException() {
        when(accountRepository.findByUsername(accountRequestDto.getUsername())).thenReturn(Optional.of(account));

        UnprocessableEntityException exception = assertThrows(UnprocessableEntityException.class, () -> {
            accountService.register(accountRequestDto);
        });
        assertEquals("User with username " + accountRequestDto.getUsername() + " already exists", exception.getMessage());
    }

    @Test
    void register_successfulRegistration_returnsAccountResponseDto() {
        when(accountRepository.findByUsername(accountRequestDto.getUsername())).thenReturn(Optional.empty());
        when(accountMapper.toEntity(accountRequestDto)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(accountResponseDto);

        AccountResponseDto result = accountService.register(accountRequestDto);

        assertNotNull(result);
        assertEquals(accountRequestDto.getUsername(), result.getUsername());
        assertEquals(accountRequestDto.getBalance(), result.getBalance());
        assertEquals(AccountStatus.ACTIVE, result.getStatus());

        verify(accountRepository, times(1)).findByUsername(accountRequestDto.getUsername());
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).toDto(account);
    }

    @Test
    void register_withMissingStatus_setsDefaultStatus() {
        accountRequestDto.setStatus(null);
        when(accountRepository.findByUsername(accountRequestDto.getUsername())).thenReturn(Optional.empty());
        when(accountMapper.toEntity(accountRequestDto)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(accountResponseDto);

        AccountResponseDto result = accountService.register(accountRequestDto);

        assertNotNull(result);
        assertEquals(AccountStatus.ACTIVE, result.getStatus());

        verify(accountRepository, times(1)).findByUsername(accountRequestDto.getUsername());
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).toDto(account);
    }

    @Test
    void register_withMissingBalance_setsDefaultBalance() {
        accountRequestDto.setBalance(null);
        when(accountRepository.findByUsername(accountRequestDto.getUsername())).thenReturn(Optional.empty());
        when(accountMapper.toEntity(accountRequestDto)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(accountResponseDto);

        AccountResponseDto result = accountService.register(accountRequestDto);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getBalance());

        verify(accountRepository, times(1)).findByUsername(accountRequestDto.getUsername());
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).toDto(account);
    }
}

