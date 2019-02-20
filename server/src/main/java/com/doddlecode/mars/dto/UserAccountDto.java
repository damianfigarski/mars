package com.doddlecode.mars.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserAccountDto {

    private Long userAccountId;
    private String email;
    private String username;
    private LocalDateTime created;
    private Set<RoleDto> roles;

}
