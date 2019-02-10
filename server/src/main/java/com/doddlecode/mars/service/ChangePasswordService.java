package com.doddlecode.mars.service;

import com.doddlecode.mars.dto.ChangePasswordDto;
import com.doddlecode.mars.exception.MarsRuntimeException;

public interface ChangePasswordService {

    void changePassword(ChangePasswordDto changePasswordDto) throws MarsRuntimeException;

}
