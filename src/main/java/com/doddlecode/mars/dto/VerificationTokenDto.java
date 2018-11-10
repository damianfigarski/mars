package com.doddlecode.mars.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"userAccount"})
public class VerificationTokenDto {

    private Long verificationTokenId;
    private String verificationToken;
    private LocalDateTime expiredDate;
    private UserAccountDto userAccount;

}
