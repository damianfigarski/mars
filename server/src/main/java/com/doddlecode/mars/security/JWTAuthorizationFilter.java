package com.doddlecode.mars.security;

import com.doddlecode.mars.exception.MarsRuntimeException;
import com.doddlecode.mars.util.JwtUtil;
import com.google.common.collect.Lists;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.doddlecode.mars.exception.code.MarsExceptionCode.E016;
import static com.doddlecode.mars.security.SecurityConstants.AUTHORITIES_KEY;
import static com.doddlecode.mars.security.SecurityConstants.HEADER_STRING;
import static com.doddlecode.mars.security.SecurityConstants.TOKEN_PREFIX;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(HEADER_STRING);
        boolean isValidHeader = isValidHeader(header);

        if (isValidHeader) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private boolean isValidHeader(String header) {
        return Optional.ofNullable(header)
                .filter(h -> !h.startsWith(TOKEN_PREFIX))
                .isPresent();
    }

    @SuppressWarnings("unchecked")
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = getToken(request.getHeader(HEADER_STRING));
        Claims claims = getUsernameFromToken(token);

        String user = claims.getSubject();
        List<String> rolesList = (List<String>) claims.get(AUTHORITIES_KEY);
        List<GrantedAuthority> authorities = getAuthorities(rolesList);

        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

    private String getToken(String headerKey) {
        return Optional.ofNullable(headerKey)
                .orElseThrow(() -> new MarsRuntimeException(E016));
    }

    private Claims getUsernameFromToken(String token) {
        return Optional.ofNullable(token)
                .map(JwtUtil::parseToken)
                .orElseThrow(() -> new MarsRuntimeException(E016));
    }

    private List<GrantedAuthority> getAuthorities(List<String> rolesList) {
        List<GrantedAuthority> authorities = Lists.newArrayList();
        for (String r : rolesList) {
            GrantedAuthority authority = new SimpleGrantedAuthority(r);
            authorities.add(authority);
        }
        return authorities;
    }

}
