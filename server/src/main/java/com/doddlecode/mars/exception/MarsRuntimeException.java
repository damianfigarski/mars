package com.doddlecode.mars.exception;

import com.doddlecode.mars.exception.code.MarsExceptionCode;
import lombok.Getter;

import java.util.ResourceBundle;

import static com.doddlecode.mars.util.HostName.hostName;

public class MarsRuntimeException extends RuntimeException {
    @Getter
    private MarsExceptionCode code;

    public MarsRuntimeException(MarsExceptionCode code) {
        super(String.format("Host: %s, Code: %s, Message: %s", hostName(), code.name(), code.message()));
        this.code = code;
    }

    public MarsRuntimeException(MarsExceptionCode code, String message) {
        super(String.format("Host: %s, Code: %s, Message: %s\n%s", hostName(), code.name(),
                ResourceBundle.getBundle("messages/messages").getString(code.message()), message));
        this.code = code;
    }

    public MarsRuntimeException(MarsExceptionCode code, Throwable cause) {
        super(String.format("Host: %s, Code: %s, Message: %s\nCause: %s", hostName(), code.name(),
                ResourceBundle.getBundle("messages/messages").getString(code.message()), cause), cause);
        this.code = code;
    }

    public MarsRuntimeException(MarsExceptionCode code, String message, Throwable cause) {
        super(String.format("Host: %s, Code: %s, Message: %s\n%s\nCause: %s", hostName(), code.name(),
                ResourceBundle.getBundle("messages/messages").getString(code.message()), message, cause), cause);
        this.code = code;
    }
}
