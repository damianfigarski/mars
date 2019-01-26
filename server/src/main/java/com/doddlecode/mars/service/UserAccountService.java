package com.doddlecode.mars.service;

import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.exception.MarsRuntimeException;

public interface UserAccountService {

    UserAccount getUserByToken(String token) throws MarsRuntimeException;
    UserAccount getById(Long id);
    UserAccount create(UserAccount userAccount) throws MarsRuntimeException;
    UserAccount update(UserAccount userAccount) throws MarsRuntimeException;

}
