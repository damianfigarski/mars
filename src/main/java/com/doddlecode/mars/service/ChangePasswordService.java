package com.doddlecode.mars.service;

import com.doddlecode.mars.dto.ChangePasswordDto;
import com.doddlecode.mars.exception.MarsRuntimeException;

import javax.servlet.http.HttpServletRequest;

public interface ChangePasswordService {

    void changePassword(ChangePasswordDto changePasswordDto, HttpServletRequest request)
            throws MarsRuntimeException;

}
