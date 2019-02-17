package com.doddlecode.mars.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserAccountDto {

    private Long userAccountId;
    private String email;
    private boolean enabled;
    private LocalDateTime created;
    private Set<RoleDto> roles;

}
