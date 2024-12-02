package com.demo.transaction.controller;

import com.demo.transaction.dto.AccountRequestDto;
import com.demo.transaction.dto.AccountResponseDto;
import com.demo.transaction.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static com.demo.transaction.model.enums.AccountStatus.ACTIVE;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountRegisterController.class)
@RequiredArgsConstructor
public class AccountRegisterControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountRegisterController accountRegisterController;

    private AccountRequestDto accountRequestDto;
    private AccountResponseDto accountResponseDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        accountRequestDto = new AccountRequestDto("chang", "chang@gmail.com", BigDecimal.ZERO, ACTIVE);
//
//        accountRequestDto = new AccountRequestDto("chang@gmail.com", "password123");
//        accountResponseDto = new AccountResponseDto(1L, "john.doe@example.com");
    }

    @Test
    public void testRegisterAccount() throws Exception {
        when(accountService.register(accountRequestDto)).thenReturn(accountResponseDto);

        mockMvc.perform(post("/api/v1/register")
                        .contentType("application/json")
                        .content("{\"email\":\"chang@example.com\",\"password\":\"1234567\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("example@gmail.com"))
                .andExpect(jsonPath("$.id").value(1));

        verify(accountService, times(1)).register(accountRequestDto);
    }

    @Test
    public void testRegisterAccountWithInvalidData() throws Exception {
        mockMvc.perform(post("/api/v1/register")
                        .contentType("application/json")
                        .content("{\"email\":\"\",\"password\":\"1234567\"}"))
                .andExpect(status().isBadRequest());

        verify(accountService, times(0)).register(any());
    }
}
