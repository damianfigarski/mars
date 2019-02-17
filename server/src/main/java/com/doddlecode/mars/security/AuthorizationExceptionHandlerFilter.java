package com.doddlecode.mars.security;

import com.doddlecode.mars.dto.ErrorDto;
import com.doddlecode.mars.exception.MarsRuntimeException;
import com.doddlecode.mars.util.MessageBundleUtil;
import com.doddlecode.mars.util.Object2Json;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (MarsRuntimeException e) {
            String code = e.getCode().name();
            String message = MessageBundleUtil.getMessage(e.getCode().getMessage());
            ErrorDto errorDto = buildErrorDto(code, message);

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(Object2Json.convert(errorDto));
        }
    }

    private ErrorDto buildErrorDto(String code, String message) {
        return ErrorDto.builder()
                .code(code)
                .message(message)
                .build();
    }

}
