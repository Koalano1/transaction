package com.demo.transaction.account;

import com.demo.transaction.controller.AccountRegisterController;
import com.demo.transaction.dto.AccountRequestDto;
import com.demo.transaction.dto.AccountResponseDto;
import com.demo.transaction.exception.UnprocessableEntityException;
import com.demo.transaction.model.enums.AccountStatus;
import com.demo.transaction.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AccountRegisterControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountRegisterController accountRegisterController;

    private MockMvc mockMvc;

    private AccountRequestDto accountRequestDto;
    private AccountResponseDto accountResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountRegisterController).build();

        // Initialize DTOs
        accountRequestDto = new AccountRequestDto();
        accountRequestDto.setUsername("testUser");
        accountRequestDto.setBalance(null);

        accountResponseDto = new AccountResponseDto();
        accountResponseDto.setUsername("testUser");
        accountResponseDto.setBalance(BigDecimal.ZERO);
        accountResponseDto.setStatus(AccountStatus.ACTIVE);
    }

    @Test
    void register_accountSuccessfully_createsAccount() throws Exception {
        when(accountService.register(accountRequestDto)).thenReturn(accountResponseDto);

        mockMvc.perform(post("/api/v1/register")
                        .contentType("application/json")
                        .content("{\"username\":\"testUser\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(accountService, times(1)).register(accountRequestDto);
    }

    @Test
    void register_accountWithExistingUsername_throwsConflict() throws Exception {
        when(accountService.register(accountRequestDto))
                .thenThrow(new UnprocessableEntityException("User with username testUser already exists"));

        mockMvc.perform(post("/api/v1/register")
                        .contentType("application/json")
                        .content("{\"username\":\"testUser\"}"))
                .andExpect(status().isConflict())  // Should return 409 Conflict
                .andExpect(jsonPath("$.message").value("User with username testUser already exists"));

        verify(accountService, times(1)).register(accountRequestDto);
    }

    @Test
    void register_accountWithNullBalance_setsDefaultBalance() throws Exception {
        accountRequestDto.setBalance(null);
        when(accountService.register(accountRequestDto)).thenReturn(accountResponseDto);

        mockMvc.perform(post("/api/v1/register")
                        .contentType("application/json")
                        .content("{\"username\":\"testUser\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(accountService, times(1)).register(accountRequestDto);
    }
}

