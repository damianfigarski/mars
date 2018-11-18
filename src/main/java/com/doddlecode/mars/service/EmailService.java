package com.doddlecode.mars.service;

import com.doddlecode.mars.exception.MarsRuntimeException;

public interface EmailService {

    void sendSimpleMessage(String to, String subject, String text);
    void sendMessage(String to, String subject, String text) throws MarsRuntimeException;

}
