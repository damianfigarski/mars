package com.doddlecode.mars.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VerificationTokenDto {

    private Long verificationTokenId;
    private String verificationToken;
    private LocalDateTime expiredDate;
    private UserAccountDto userAccount;

}
