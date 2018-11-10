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
public class UserLogDto {

    private Long userLogId;
    private LocalDateTime logDate;
    private String ipAddress;
    private UserAccountDto userAccount;

}
