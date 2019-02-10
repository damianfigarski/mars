package com.doddlecode.mars.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserAccountCreateDto {

    private Long userAccountId;
    private String email;
    private String password;
    private boolean enabled;
    private String fullName;
    private LocalDateTime created;
    private Set<RoleDto> roles;

}
