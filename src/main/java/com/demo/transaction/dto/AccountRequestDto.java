package com.demo.transaction.dto;

import com.demo.transaction.model.enums.AccountStatus;
import lombok.*;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountRequestDto {

    private String username;

    private String email;

    private BigDecimal balance;

    private AccountStatus status;

}
