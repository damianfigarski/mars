package com.doddlecode.mars.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Set;

@Data
public class RoleDto {

    private Long roleId;
    private String roleName;
    @JsonIgnore
    private Set<UserAccountDto> users;

}
