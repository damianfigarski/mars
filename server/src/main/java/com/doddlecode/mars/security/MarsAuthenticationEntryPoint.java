package com.doddlecode.mars.security;

import com.doddlecode.mars.dto.ErrorDto;
import com.doddlecode.mars.util.Object2Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Slf4j
public class MarsAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) throws IOException {
        log.error(e.getMessage(), e);
        ErrorDto errorDto = buildErrorDto();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader(CONTENT_TYPE, APPLICATION_JSON);
        response.getWriter().write(Object2Json.convert(errorDto));
    }

    private ErrorDto buildErrorDto() {
        return ErrorDto.builder()
                .message("Unauthorized")
                .build();
    }

}
