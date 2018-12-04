package com.doddlecode.mars.service;

import com.doddlecode.mars.dto.ForgetPasswordHelperDto;
import com.doddlecode.mars.exception.MarsRuntimeException;

public interface ForgetPasswordService {

    void sendEmailWithChangingPasswordCredentials(String email) throws MarsRuntimeException;
    void forgetPassword(ForgetPasswordHelperDto forgetPasswordHelperDto) throws MarsRuntimeException;

}
