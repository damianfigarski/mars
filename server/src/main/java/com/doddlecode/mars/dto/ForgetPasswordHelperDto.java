package com.doddlecode.mars.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForgetPasswordHelperDto {

    private String email;
    private String token;
    private String password;

}
