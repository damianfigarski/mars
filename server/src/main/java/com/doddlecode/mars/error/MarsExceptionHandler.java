package com.doddlecode.mars.error;

import com.doddlecode.mars.exception.MarsRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ResourceBundle;

@ControllerAdvice
public class MarsExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(MarsExceptionHandler.class);

    @ExceptionHandler({MarsRuntimeException.class})
    public ResponseEntity<Object> handleMarsException(final MarsRuntimeException e, final WebRequest request) {
        final String bodyOfResponse = ResourceBundle.getBundle("messages/messages").getString(e.getCode().message());
        logger.error(e.getMessage(), e);
        return handleExceptionInternal(e, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}
