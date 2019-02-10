package com.doddlecode.mars.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserLogDto {

    private Long userLogId;
    private LocalDateTime logDate;
    private String ipAddress;
    private UserAccountDto userAccount;

}
