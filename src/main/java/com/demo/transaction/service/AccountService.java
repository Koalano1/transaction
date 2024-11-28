package com.demo.transaction.service;

import com.demo.transaction.dto.AccountRequestDto;
import com.demo.transaction.dto.AccountResponseDto;

public interface AccountService {

    AccountResponseDto register(AccountRequestDto request);

}
