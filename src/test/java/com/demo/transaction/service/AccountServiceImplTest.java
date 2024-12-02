package com.demo.transaction.service;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class AccountServiceImplTest {
    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    private AccountRequestDto accountRequestDto;
    private AccountResponseDto accountResponseDto;
    private Account account;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        accountRequestDto = new AccountRequestDto("chang", "chang@gmail.com", BigDecimal.ZERO, AccountStatus.ACTIVE);
//        accountResponseDto = new AccountResponseDto("chang1", "chang@gmail.com", BigDecimal.ZERO, AccountStatus.ACTIVE);
//        account = new Account(1L, "john.doe", "password123", AccountStatus.ACTIVE, BigDecimal.ZERO);
    }

    @Test
    public void testRegisterAccountSuccessfully() {
        when(accountRepository.findByUsername(accountRequestDto.getUsername())).thenReturn(Optional.empty());

        when(accountMapper.toEntity(accountRequestDto)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(accountResponseDto);

        AccountResponseDto result = accountService.register(accountRequestDto);

        assertNotNull(result);
        assertEquals(accountResponseDto.getUsername(), result.getUsername());
        assertEquals(accountResponseDto.getStatus(), result.getStatus());
        assertEquals(accountResponseDto.getBalance(), result.getBalance());

        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testRegisterAccountWithExistingUsername() {
        when(accountRepository.findByUsername(accountRequestDto.getUsername())).thenReturn(Optional.of(account));

        UnprocessableEntityException exception = assertThrows(UnprocessableEntityException.class, () -> {
            accountService.register(accountRequestDto);
        });

        assertEquals("User with username john.doe already exists", exception.getMessage());

        verify(accountRepository, times(0)).save(any());
    }

    @Test
    public void testRegisterAccountWithDefaultStatusAndBalance() {
        accountRequestDto.setStatus(null);
        accountRequestDto.setBalance(null);

        when(accountRepository.findByUsername(accountRequestDto.getUsername())).thenReturn(Optional.empty());

        when(accountMapper.toEntity(accountRequestDto)).thenReturn(account);
        when(accountMapper.toDto(account)).thenReturn(accountResponseDto);

        AccountResponseDto result = accountService.register(accountRequestDto);

        assertEquals(AccountStatus.ACTIVE, result.getStatus());
        assertEquals(BigDecimal.ZERO, result.getBalance());
    }
}
