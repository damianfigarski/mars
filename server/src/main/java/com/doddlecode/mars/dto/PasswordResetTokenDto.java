package com.doddlecode.mars.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PasswordResetTokenDto {

    private Long passwordResetTokenId;
    private String token;
    private boolean used;
    private LocalDateTime expiredDate;
    private UserAccountDto userAccount;

}
