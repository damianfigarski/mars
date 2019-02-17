package com.doddlecode.mars.security;

import com.doddlecode.mars.entity.UserAccount;
import com.doddlecode.mars.exception.MarsRuntimeException;
import com.doddlecode.mars.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.doddlecode.mars.exception.code.MarsExceptionCode.E013;
import static com.doddlecode.mars.security.SecurityConstants.HEADER_STRING;
import static com.doddlecode.mars.security.SecurityConstants.TOKEN_PREFIX;

@RequiredArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            UserAccount creds = new ObjectMapper()
                    .readValue(request.getInputStream(), UserAccount.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword()
                    )
            );
        } catch (IOException e) {
            throw new MarsRuntimeException(E013, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication auth) {
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) auth.getAuthorities();
        List<String> roles = Lists.newArrayList();

        for (GrantedAuthority ga : authorities) {
            roles.add(ga.getAuthority());
        }

        String username = ((User) auth.getPrincipal()).getUsername();
        String token = JwtUtil.buildToken(username, roles);

        response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }
}
