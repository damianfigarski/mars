package com.doddlecode.mars.error;

import com.doddlecode.mars.dto.ErrorDto;
import com.doddlecode.mars.exception.MarsRuntimeException;
import com.doddlecode.mars.exception.code.MarsExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ResourceBundle;

@Slf4j
@ControllerAdvice
public class MarsExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({MarsRuntimeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto handleMarsException(MarsRuntimeException e) {
        logger.error(e.getMessage(), e);
        return buildExceptionDto(e.getCode());
    }

    private ErrorDto buildExceptionDto(MarsExceptionCode c) {
        String message = ResourceBundle.getBundle("messages/messages").getString(c.getMessage());
        return ErrorDto.builder()
                .code(c.name())
                .message(message)
                .build();
    }

}
