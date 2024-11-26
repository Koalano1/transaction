package com.demo.transaction.dto;

import com.demo.transaction.model.enums.AccountStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AccountResponseDto {

    private Long id;

    private String username;

    private String email;

    private BigDecimal balance;

    private AccountStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
