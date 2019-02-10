package com.doddlecode.mars.util;

import com.doddlecode.mars.exception.MarsRuntimeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

import static com.doddlecode.mars.exception.code.MarsExceptionCode.E017;
import static com.doddlecode.mars.exception.code.MarsExceptionCode.E018;
import static com.doddlecode.mars.security.SecurityConstants.AUTHORITIES_KEY;
import static com.doddlecode.mars.security.SecurityConstants.EXPIRATION_TIME;
import static com.doddlecode.mars.security.SecurityConstants.SECRET;
import static com.doddlecode.mars.security.SecurityConstants.TOKEN_PREFIX;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtil {

    public static String buildToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim(AUTHORITIES_KEY, roles)
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
                .compact();
    }

    public static Claims parseToken(String token) throws MarsRuntimeException {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET.getBytes())
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();
        } catch (SignatureException se) {
            throw new MarsRuntimeException(E017);
        } catch (ExpiredJwtException eje) {
            throw new MarsRuntimeException(E018);
        }
    }

}
