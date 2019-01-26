package com.doddlecode.mars.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"roles"})
public class UserAccountDto {

    private Long userAccountId;
    private String email;
    private boolean enabled;
    private String fullName;
    private LocalDateTime created;
    private Set<RoleDto> roles;

}
